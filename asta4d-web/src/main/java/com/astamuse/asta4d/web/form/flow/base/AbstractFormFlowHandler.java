package com.astamuse.asta4d.web.form.flow.base;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    protected abstract T createInitForm();

    protected String handleWithRenderTargetStep() throws Exception {
        return (String) handle(true);
    }

    protected CommonFormResult handleWithCommonFormResult() throws Exception {
        return (CommonFormResult) handle(false);
    }

    protected <D> void saveExtraDataToContext(D actionInfo) {
        Context.getCurrentThreadContext().setData(FORM_EXTRA_DATA, actionInfo);
    }

    protected <D> D getExtraDataFromContext() {
        return Context.getCurrentThreadContext().getData(FORM_EXTRA_DATA);
    }

    private void savePreDefinedForm(T form) {
        Context.getCurrentThreadContext().setData(FORM_PRE_DEFINED, form);
    }

    protected Object handle(boolean returnRenderTargetStep) throws Exception {
        FormProcessData processData = (FormProcessData) InjectUtil.retrieveContextDataSetInstance(formProcessDataCls,
                "not-exist-IntelligentFormProcessData", "");

        String traceData = processData.getStepTraceData();

        if (processData.getStepExit() != null) {
            clearSavedTraceMap(traceData);
            return null;
        }

        String currentStep = processData.getStepCurrent();

        Map<String, Object> traceMap;

        if (StringUtils.isEmpty(traceData)) {
            traceMap = new HashMap<>();
        } else {
            traceMap = restoreTraceMap(traceData);
            if (traceMap == null) {
                traceMap = new HashMap<>();
            }
        }

        // the first time access without existing input data or saved tracemap could not be retrieved(usually due to timeout)
        if (currentStep == null || traceMap.isEmpty()) {
            currentStep = FormFlowConstants.FORM_STEP_INIT_STEP;
            savePreDefinedForm(createInitForm());
        } else {
        }

        T form = retrieveFormInstance(traceMap, currentStep);

        traceMap.put(currentStep, form);

        String renderTargetStep = null;
        CommonFormResult formResult = null;

        if (processData.getStepBack() != null) {
            renderTargetStep = processData.getStepBack();
            traceMap.remove(currentStep);
            passDataToSnippet(currentStep, renderTargetStep, traceMap, null);
        } else {
            if (FormFlowConstants.FORM_STEP_INIT_STEP.equals(currentStep)) {
                renderTargetStep = FormFlowConstants.FORM_STEP_INIT_STEP;
            } else {
                formResult = handle(currentStep, form);
                if (formResult == CommonFormResult.SUCCESS) {
                    renderTargetStep = processData.getStepSuccess();
                } else {
                    renderTargetStep = processData.getStepFailed();
                }
            }
            passDataToSnippet(currentStep, renderTargetStep, traceMap, formResult);
        }

        if (isCompleteStep(renderTargetStep)) {
            WebApplicationContext context = WebApplicationContext.getCurrentThreadWebApplicationContext();
            String newTraceData = context.getData(FormFlowConstants.FORM_STEP_TRACE_MAP_STR);
            clearSavedTraceMap(newTraceData);
        }

        if (returnRenderTargetStep) {
            return renderTargetStep;
        } else {
            return formResult;
        }
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
     * <b>NOTE:</b> DO NOT replace this method completely at sub class, if you want to do some cusomized form retrieving, override the
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

                    final Integer len = (Integer) arrayLengthField.retrieveValue(form);
                    if (len == null) {
                        throw new NullPointerException("specified array length field [" + cff.arrayLengthField() + "] is null");
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

    protected String rewriteArrayIndexPlaceHolder(String s, int seq) {
        return CascadeFormUtil.rewriteArrayIndexPlaceHolder(s, seq);
    }

    protected String saveTraceMap(Map<String, Object> traceMap) {
        String id = SecureIdGenerator.createEncryptedURLSafeId();
        WebApplicationConfiguration.getWebApplicationConfiguration().getTimeoutDataManager()
                .put(id, traceMap, cachedTraceMapLivingTimeInMilliSeconds());
        return id;
    }

    protected Map<String, Object> restoreTraceMap(String data) {
        return WebApplicationConfiguration.getWebApplicationConfiguration().getTimeoutDataManager().get(data);
    }

    protected void clearSavedTraceMap(String traceData) {
        if (StringUtils.isNotEmpty(traceData)) {
            WebApplicationConfiguration.getWebApplicationConfiguration().getTimeoutDataManager().get(traceData);
        }
    }

    protected long cachedTraceMapLivingTimeInMilliSeconds() {
        // 30 minutes
        return 30 * 60 * 1000L;
    }

    protected void passDataToSnippet(String currentStep, String renderTargetStep, Map<String, Object> traceMap, CommonFormResult result) {
        T form = (T) traceMap.get(currentStep);
        WebApplicationContext context = WebApplicationContext.getCurrentThreadWebApplicationContext();

        boolean byFlash = passDataToSnippetByFlash(currentStep, renderTargetStep, form, result);

        String traceData = saveTraceMap(traceMap);

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

    protected boolean passDataToSnippetByFlash(String currentStep, String renderTargetStep, T form, CommonFormResult result) {
        return false;
    }

    protected abstract boolean isCompleteStep(String step);

    protected FormValidator getTypeUnMatchValidator() {
        return new TypeUnMatchValidator();
    }

    protected FormValidator getValueValidator() {
        return new JsrValidator();
    }

    protected List<FormValidationMessage> validate(T form) {
        List<FormValidationMessage> validationMessages = new LinkedList<>();

        validationMessages.addAll(getTypeUnMatchValidator().validate(form));
        if (!validationMessages.isEmpty()) {
            return validationMessages;
        }

        validationMessages.addAll(getValueValidator().validate(form));
        return validationMessages;
    }

    protected CommonFormResult handle(String currentStep, T form) {
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

    protected void outputValidationMessage(FormValidationMessage msg) {
        DefaultMessageRenderingHelper.getConfiguredInstance().err("#" + msg.getName() + "-err-msg", msg.getMessage());
    }
}
