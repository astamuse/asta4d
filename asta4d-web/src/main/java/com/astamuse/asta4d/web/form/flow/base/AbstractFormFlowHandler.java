package com.astamuse.asta4d.web.form.flow.base;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.InjectTrace;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.RedirectInterceptor;
import com.astamuse.asta4d.web.dispatch.RedirectUtil;
import com.astamuse.asta4d.web.form.CascadeFormUtil;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.flow.classical.MultiStepFormFlowHandler;
import com.astamuse.asta4d.web.form.validation.FormValidationMessage;
import com.astamuse.asta4d.web.form.validation.FormValidator;
import com.astamuse.asta4d.web.form.validation.JsrValidator;
import com.astamuse.asta4d.web.form.validation.TypeUnMatchValidator;
import com.astamuse.asta4d.web.util.SecureIdGenerator;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class AbstractFormFlowHandler<T> {

    private static final String FORM_PRE_DEFINED = "FORM_PRE_DEFINED#" + AbstractFormFlowHandler.class.getName();

    private static final String FORM_EXTRA_DATA = "FORM_EXTRA_DATA#" + AbstractFormFlowHandler.class.getName();

    public static final String PRE_INJECTION_TRACE_INFO = "PRE_INJECTION_TRACE_INFO#" + AbstractFormFlowHandler.class.getName();

    private Class<? extends FormProcessData> formProcessDataCls;
    private Class formCls;

    public AbstractFormFlowHandler(Class<T> formCls) {
        this(formCls, SimpleFormProcessData.class);
    }

    public AbstractFormFlowHandler(Class<T> formCls, Class<? extends FormProcessData> formProcessDataCls) {
        this.formCls = formCls;
        this.formProcessDataCls = formProcessDataCls;
    }

    /**
     * Sub classes must tell us whether the given step should be treated as the last step(to complete) of current form flow.
     * 
     * @param step
     * @return
     */
    protected abstract boolean isCompleteStep(String step);

    /**
     * Sub classes could override this method to create the initial form data(eg. query from db)
     * 
     * @return
     * @throws Exception
     */
    protected T createInitForm() throws Exception {
        return (T) InjectUtil.retrieveContextDataSetInstance(formCls, FORM_PRE_DEFINED, "");
    }

    /**
     * Convenience for saving some extra data in context in case of being necessary
     * 
     * @param actionInfo
     * @see #getExtraDataFromContext()
     */
    protected <D> void saveExtraDataToContext(D actionInfo) {
        Context.getCurrentThreadContext().setData(FORM_EXTRA_DATA, actionInfo);
    }

    /**
     * Convenience for retrieving some extra data in context in case of being necessary
     * 
     * @param actionInfo
     * @see #saveExtraDataToContext(Object)
     */
    protected <D> D getExtraDataFromContext() {
        return Context.getCurrentThreadContext().getData(FORM_EXTRA_DATA);
    }

    private void savePreDefinedForm(T form) {
        Context.getCurrentThreadContext().setData(FORM_PRE_DEFINED, form);
    }

    /**
     * Sub classes could override this method to translate the returned target step to the actual render target template file path.
     * 
     * @return the render target step name
     * @throws Exception
     */
    protected String handle() throws Exception {
        FormProcessData processData = (FormProcessData) InjectUtil.retrieveContextDataSetInstance(formProcessDataCls,
                "not-exist-IntelligentFormProcessData", "");

        String traceData = processData.getStepTraceData();

        if (processData.getStepExit() != null) {
            clearSavedTraceMap(traceData);
            return null;
        }

        Map<String, Object> traceMap;

        if (StringUtils.isEmpty(traceData)) {
            traceMap = new HashMap<>();
        } else {
            traceMap = restoreTraceMap(traceData);
            if (traceMap == null) {
                traceMap = new HashMap<>();
            }
        }

        String currentStep = processData.getStepCurrent();
        // the first time access without existing input data or saved tracemap could not be retrieved(usually due to timeout)
        if (currentStep == null) {
            currentStep = FormFlowConstants.FORM_STEP_INIT_STEP;
            savePreDefinedForm(createInitForm());
        }

        T form = retrieveFormInstance(traceMap, currentStep);

        traceMap.put(currentStep, form);

        String renderTargetStep = null;
        CommonFormResult formResult = null;

        if (processData.getStepBack() != null) {
            renderTargetStep = processData.getStepBack();
            traceMap.remove(currentStep);
            passDataToSnippet(currentStep, renderTargetStep, traceMap);
        } else {
            if (FormFlowConstants.FORM_STEP_INIT_STEP.equals(currentStep)) {
                formResult = CommonFormResult.INIT;
                renderTargetStep = FormFlowConstants.FORM_STEP_INIT_STEP;
            } else {
                // since the init step will not enter this branch, so the sub classes which override the process method could retrieve
                // current step without any concern about null pointer exception.
                formResult = process(processData, form);
                if (formResult == CommonFormResult.SUCCESS) {
                    renderTargetStep = processData.getStepSuccess();
                } else {
                    renderTargetStep = processData.getStepFailed();
                }
            }
            passDataToSnippet(currentStep, renderTargetStep, traceMap);
        }

        if (isCompleteStep(renderTargetStep)) {
            WebApplicationContext context = WebApplicationContext.getCurrentThreadWebApplicationContext();
            String newTraceData = context.getData(FormFlowConstants.FORM_STEP_TRACE_MAP_STR);
            clearSavedTraceMap(newTraceData);
        }

        return renderTargetStep;

    }

    /**
     * Sub classes can override this method to customize how to retrieve the form instance by step information.
     * 
     * @param traceMap
     * @param currentStep
     * @return
     */
    protected T retrieveFormInstance(Map<String, Object> traceMap, String currentStep) {
        // The subclass may override this method to retrieving form instance by various ways but we will always generate an instance from
        // the context since we have no idea about the concrete logic of sub classes.
        return generateFormInstanceFromContext();
    }

    /**
     * Sub classes can override this method to do some interception around form instance generation, especially some post processes.
     * <p>
     * <b>NOTE:</b> DO NOT replace this method completely at sub class, if you want to do some customized form retrieving, override the
     * method {@link #retrieveFormInstance(Map, String)} instead.
     * 
     * @return
     */
    protected T generateFormInstanceFromContext() {
        try {

            final T form = (T) InjectUtil.retrieveContextDataSetInstance(formCls, FORM_PRE_DEFINED, "");
            List<AnnotatedPropertyInfo> list = AnnotatedPropertyUtil.retrieveProperties(formCls);
            Context currentContext = Context.getCurrentThreadContext();
            for (final AnnotatedPropertyInfo field : list) {
                CascadeFormField cff = field.getAnnotation(CascadeFormField.class);
                if (cff != null) {
                    if (field.retrieveValue(form) != null) {
                        continue;
                    }

                    if (StringUtils.isEmpty(cff.arrayLengthField())) {
                        continue;
                    }

                    AnnotatedPropertyInfo arrayLengthField = AnnotatedPropertyUtil.retrievePropertyByName(formCls, cff.arrayLengthField());
                    if (arrayLengthField == null) {
                        throw new NullPointerException("specified array length field [" + cff.arrayLengthField() + "] was not found");
                    }

                    Integer len = (Integer) arrayLengthField.retrieveValue(form);
                    if (len == null) {
                        // throw new NullPointerException("specified array length field [" + cff.arrayLengthField() + "] is null");
                        len = 0;
                    }

                    final Object[] array = (Object[]) Array.newInstance(field.getType().getComponentType(), len);
                    for (int i = 0; i < len; i++) {
                        final int seq = i;
                        Context.with(new DelatedContext(currentContext) {
                            protected String convertKey(String scope, String key) {
                                if (scope.equals(WebApplicationContext.SCOPE_QUERYPARAM)) {
                                    return rewriteArrayIndexPlaceHolder(key, seq);
                                } else {
                                    return key;
                                }
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Object subform = field.getType().getComponentType().newInstance();
                                    InjectUtil.injectToInstance(subform);
                                    Array.set(array, seq, subform);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });// end runnable and context.with
                    }// end for loop

                    field.assginValue(form, array);
                }
            }
            return form;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sub classes can override this method to supply a customized array index placeholder mechanism.
     * 
     * @param s
     * @param seq
     * @return
     * @see AbstractFormFlowSnippet#rewriteArrayIndexPlaceHolder(String, int)
     */
    protected String rewriteArrayIndexPlaceHolder(String s, int seq) {
        return CascadeFormUtil.rewriteArrayIndexPlaceHolder(s, seq);
    }

    /**
     * <b>Note</b>: In fact, we should not save the trace map when some steps such as init step to avoid unnecessary memory usage, thus we
     * call the {@link #skipSaveTraceMap(String, String, Map)} to decide save or not.
     * 
     * In other words ,the sub class have the responsibility to tell us save or not by overriding the method
     * {@link #skipSaveTraceMap(String, String, Map)}.
     * 
     * @param currentStep
     * @param renderTargetStep
     * @param traceMap
     * @return
     */
    protected String saveTraceMap(String currentStep, String renderTargetStep, Map<String, Object> traceMap) {
        if (skipSaveTraceMap(currentStep, renderTargetStep, traceMap)) {
            return "";
        } else {
            String id = SecureIdGenerator.createEncryptedURLSafeId();
            WebApplicationConfiguration.getWebApplicationConfiguration().getTimeoutDataManager()
                    .put(id, traceMap, cachedTraceMapLivingTimeInMilliSeconds());
            return id;
        }
    }

    /**
     * Since we are lacking of necessary step information to judge if we should save or not, we only do the basic judgment for the init
     * step. The sub class have the responsibility to handle other cases.
     * 
     * @see MultiStepFormFlowHandler#skipSaveTraceMap(String, String, Map)
     * 
     */
    protected boolean skipSaveTraceMap(String currentStep, String renderTargetStep, Map<String, Object> traceMap) {
        if (FormFlowConstants.FORM_STEP_INIT_STEP.equals(currentStep)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * retrieve the stored trace map.
     * 
     * @param traceData
     * @return
     * @see #saveTraceMap(String, String, Map)
     */
    protected Map<String, Object> restoreTraceMap(String traceData) {
        return WebApplicationConfiguration.getWebApplicationConfiguration().getTimeoutDataManager().get(traceData);
    }

    /**
     * 
     * clear the stored trace map.
     * 
     * @param traceData
     * @see #saveTraceMap(String, String, Map)
     */
    protected void clearSavedTraceMap(String traceData) {
        if (StringUtils.isNotEmpty(traceData)) {
            WebApplicationConfiguration.getWebApplicationConfiguration().getTimeoutDataManager().get(traceData);
        }
    }

    /**
     * Sub classes can override this method to customize how long the form flow trace data will keep alive.
     * <p>
     * The default value is 30 minutes.
     * 
     * @return
     */
    protected long cachedTraceMapLivingTimeInMilliSeconds() {
        // 30 minutes
        return 30 * 60 * 1000L;
    }

    /**
     * Sub classes can override this method to customize how to pass data to snippet.
     * <p>
     * This method will retrieve the form of render target from trace map and if it does not exists, the form of current step will be used.
     * By default, the render target form is not set so the current step form will be used always. The sub classes could override this
     * method to store the render target form to trace map before calling super.
     * 
     * @param currentStep
     * @param renderTargetStep
     * @param traceMap
     */
    protected void passDataToSnippet(String currentStep, String renderTargetStep, Map<String, Object> traceMap) {
        T form = (T) traceMap.get(renderTargetStep);
        if (form == null) {
            form = (T) traceMap.get(currentStep);
        }
        WebApplicationContext context = WebApplicationContext.getCurrentThreadWebApplicationContext();

        boolean byFlash = passDataToSnippetByFlash(currentStep, renderTargetStep, form);

        String traceData = saveTraceMap(currentStep, renderTargetStep, traceMap);

        passData(context, byFlash, FormFlowConstants.FORM_STEP_TRACE_MAP, traceMap);
        passData(context, byFlash, FormFlowConstants.FORM_STEP_TRACE_MAP_STR, traceData);
        passData(context, byFlash, FormFlowConstants.FORM_STEP_RENDER_TARGET, renderTargetStep);

        if (byFlash) {

            RedirectUtil.registerRedirectInterceptor(this.getClass().getName() + "#passDataToSnippet", new RedirectInterceptor() {
                @Override
                public void beforeRedirect() {
                    RedirectUtil.addFlashScopeData(PRE_INJECTION_TRACE_INFO, InjectTrace.retrieveTraceList());
                }

                @Override
                public void afterRedirectDataRestore() {
                    List list = (List) Context.getCurrentThreadContext().getData(WebApplicationContext.SCOPE_FLASH,
                            PRE_INJECTION_TRACE_INFO);
                    InjectTrace.restoreTraceList(list);

                }
            });

            // used by clearSavedTraceMap
            context.setData(FormFlowConstants.FORM_STEP_TRACE_MAP_STR, traceData);
        }
    }

    private void passData(WebApplicationContext context, boolean byFlash, String key, Object data) {
        if (byFlash) {
            RedirectUtil.addFlashScopeData(key, data);
        } else {
            context.setData(key, data);
        }
    }

    /**
     * Sub classes should tell us whether we should pass data to snippet via flash scope. The default is false.
     * 
     * @param currentStep
     * @param renderTargetStep
     * @param form
     * @param result
     * @return
     */
    protected boolean passDataToSnippetByFlash(String currentStep, String renderTargetStep, T form) {
        return false;
    }

    /**
     * The default process will only call the {@link #processValidation(Object)} and sub classes can override this method to add extra
     * process logics such as updating form when validation succeeds.
     * 
     * @param processData
     * @param form
     * @return
     */
    protected CommonFormResult process(FormProcessData processData, T form) {
        return processValidation(processData, form);
    }

    /**
     * Sub classes can override this method to customize how to handle the validation result
     * 
     * @param form
     * @return
     */
    protected CommonFormResult processValidation(FormProcessData processData, T form) {
        List<FormValidationMessage> validationMesssages = validate(form);
        if (validationMesssages.isEmpty()) {
            return CommonFormResult.SUCCESS;
        } else {
            for (FormValidationMessage msg : validationMesssages) {
                outputValidationMessage(msg);
            }
            return CommonFormResult.FAILED;
        }
    }

    /**
     * Sub classes can override this method to customize how to output validation messages
     * 
     * @param msg
     */
    protected void outputValidationMessage(FormValidationMessage msg) {
        DefaultMessageRenderingHelper.getConfiguredInstance().err("#" + msg.getFieldName() + "-err-msg", msg.getMessage());
    }

    /**
     * 
     * Sub classes can override this method to supply customized validation mechanism.
     * 
     * @param form
     * @return
     */
    protected List<FormValidationMessage> validate(Object form) {
        List<FormValidationMessage> validationMessages = new LinkedList<>();

        Set<String> fieldNameSet = new HashSet<String>();

        List<FormValidationMessage> typeMessages = getTypeUnMatchValidator().validate(form);
        for (FormValidationMessage message : typeMessages) {
            validationMessages.add(message);
            fieldNameSet.add(message.getFieldName());
        }

        List<FormValidationMessage> valueMessages = getValueValidator().validate(form);

        // there may be a not null/empty value validation error for the fields which has been validated as type unmatch, we simply remove
        // them.

        for (FormValidationMessage message : valueMessages) {
            if (!fieldNameSet.contains(message.getFieldName())) {
                validationMessages.add(message);
            }
        }

        return validationMessages;
    }

    /**
     * Sub classes can override this method to supply a customized type unmatch validator
     * 
     * @return
     */
    protected FormValidator getTypeUnMatchValidator() {
        return new TypeUnMatchValidator();
    }

    /**
     * Sub classes can override this method to supply a customized value validator
     * 
     * @return
     */
    protected FormValidator getValueValidator() {
        return new JsrValidator();
    }

}
