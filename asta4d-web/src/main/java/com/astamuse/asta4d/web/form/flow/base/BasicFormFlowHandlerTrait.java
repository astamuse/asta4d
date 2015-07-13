/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.astamuse.asta4d.web.form.flow.base;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
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
import com.astamuse.asta4d.web.form.CascadeArrayFunctions;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.validation.FormValidationMessage;
import com.astamuse.asta4d.web.form.validation.FormValidator;
import com.astamuse.asta4d.web.form.validation.JsrValidator;
import com.astamuse.asta4d.web.form.validation.TypeUnMatchValidator;
import com.astamuse.asta4d.web.util.SecureIdGenerator;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

@SuppressWarnings({ "rawtypes", "unchecked" })
public interface BasicFormFlowHandlerTrait<T> extends CascadeArrayFunctions {

    public static final String FORM_PRE_DEFINED = "FORM_PRE_DEFINED#" + BasicFormFlowHandlerTrait.class.getName();

    public static final String FORM_EXTRA_DATA = "FORM_EXTRA_DATA#" + BasicFormFlowHandlerTrait.class.getName();

    public static final String PRE_INJECTION_TRACE_INFO = "PRE_INJECTION_TRACE_INFO#" + BasicFormFlowHandlerTrait.class.getName();

    public Class<T> getFormCls();

    default Class<? extends FormProcessData> getFormProcessDataCls() {
        return SimpleFormProcessData.class;
    }

    /**
     * Sub classes must tell us the name of first step
     * 
     * @param step
     * @return
     */
    public String firstStepName();

    /**
     * Sub classes must tell us whether the name of complete step which means to finish current flow
     * 
     * @param step
     * @return
     */
    public String completeStepName();

    /**
     * Sub classes could override this method to create the initial form data(eg. query from db)
     * 
     * @return
     * @throws Exception
     */
    default T createInitForm() throws Exception {
        return (T) InjectUtil.retrieveContextDataSetInstance(getFormCls(), FORM_PRE_DEFINED, "");
    }

    /**
     * Convenience for saving some extra data in context in case of being necessary
     * 
     * @param actionInfo
     * @see #getExtraDataFromContext()
     */
    default <D> void saveExtraDataToContext(D actionInfo) {
        Context.getCurrentThreadContext().setData(FORM_EXTRA_DATA, actionInfo);
    }

    /**
     * Convenience for retrieving some extra data in context in case of being necessary
     * 
     * @param actionInfo
     * @see #saveExtraDataToContext(Object)
     */
    default <D> D getExtraDataFromContext() {
        return Context.getCurrentThreadContext().getData(FORM_EXTRA_DATA);
    }

    /**
     * Sub classes could override this method to translate the returned target step to the actual render target template file path.
     * 
     * @return the render target step name
     * @throws Exception
     */
    default String handle() throws Exception {
        FormProcessData processData = (FormProcessData) InjectUtil.retrieveContextDataSetInstance(getFormProcessDataCls(),
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
            currentStep = FormFlowConstants.FORM_STEP_BEFORE_FIRST;
            // save init form as predefined form
            Context.getCurrentThreadContext().setData(FORM_PRE_DEFINED, createInitForm());
        }

        T form = retrieveFormInstance(traceMap, currentStep);

        traceMap.put(currentStep, form);

        String renderTargetStep = null;
        CommonFormResult formResult = null;

        if (processData.getStepBack() != null) {
            renderTargetStep = processData.getStepBack();
            if (removeCurrentStepDataFromTraceMapWhenStepBack(currentStep, renderTargetStep)) {
                traceMap.remove(currentStep);
            }
            passDataToSnippet(currentStep, renderTargetStep, traceMap);
        } else {
            if (FormFlowConstants.FORM_STEP_BEFORE_FIRST.equals(currentStep)) {
                renderTargetStep = firstStepName();
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

        if (completeStepName().equalsIgnoreCase(renderTargetStep)) {
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
    default T retrieveFormInstance(Map<String, Object> traceMap, String currentStep) {
        // The subclass may override this method to retrieving form instance by various ways but we will always generate an instance from
        // the context since we have no idea about the concrete logic of sub classes.
        return generateFormInstanceFromContext(currentStep);
    }

    /**
     * Sub classes can override this method to do some interception around form instance generation, especially some post processes.
     * <p>
     * <b>NOTE:</b> DO NOT replace this method completely at sub class, if you want to do some customized form retrieving, override the
     * method {@link #retrieveFormInstance(Map, String)} instead.
     * 
     * @param currentStep
     * @return
     */
    default T generateFormInstanceFromContext(String currentStep) {
        try {
            final T form = (T) InjectUtil.retrieveContextDataSetInstance(getFormCls(), FORM_PRE_DEFINED, "");
            Context currentContext = Context.getCurrentThreadContext();

            return assignArrayValueFromContext(getFormCls(), form, currentContext, EMPTY_INDEXES);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Assign array value to cascade forms of array type from context by recursively self call.
     * 
     * @param formCls
     * @param form
     * @param currentContext
     * @param indexes
     * @return
     * @throws Exception
     */
    default T assignArrayValueFromContext(Class formCls, T form, Context currentContext, int[] indexes) throws Exception {
        List<AnnotatedPropertyInfo> list = AnnotatedPropertyUtil.retrieveProperties(formCls);
        for (final AnnotatedPropertyInfo field : list) {
            CascadeFormField cff = field.getAnnotation(CascadeFormField.class);
            if (cff != null) {
                if (field.getType().isArray()) {// a cascade form for array
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
                        final int[] newIndex = ArrayUtils.add(indexes, seq);
                        Context.with(new DelatedContext(currentContext) {
                            protected String convertKey(String scope, String key) {
                                if (scope.equals(WebApplicationContext.SCOPE_QUERYPARAM)) {
                                    return rewriteArrayIndexPlaceHolder(key, newIndex);
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

                        assignArrayValueFromContext(field.getType().getComponentType(), (T) array[seq], currentContext, newIndex);
                    }// end for loop

                    field.assignValue(form, array);
                } else {
                    // a cascade form for not array
                    assignArrayValueFromContext(field.getType(), (T) field.retrieveValue(form), currentContext, indexes);
                }
            }
        }
        return form;
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
    default String saveTraceMap(String currentStep, String renderTargetStep, Map<String, Object> traceMap) {
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
     * The sub classes could tell us whether we should remove the current step data from trace map when we back to the previous step. By
     * default, it returns true always.
     * 
     * @param currentStep
     * @param renderTargetStep
     * @return
     */
    default boolean removeCurrentStepDataFromTraceMapWhenStepBack(String currentStep, String renderTargetStep) {
        return true;
    }

    /**
     * Since we are lacking of necessary step information to judge if we should save or not, we only do the basic judgment for the init
     * step. The sub class have the responsibility to handle other cases.
     * 
     * @see MultiStepFormFlowHandler#skipSaveTraceMap(String, String, Map)
     * 
     */
    default boolean skipSaveTraceMap(String currentStep, String renderTargetStep, Map<String, Object> traceMap) {
        if (FormFlowConstants.FORM_STEP_BEFORE_FIRST.equals(currentStep)) {
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
    default Map<String, Object> restoreTraceMap(String traceData) {
        return WebApplicationConfiguration.getWebApplicationConfiguration().getTimeoutDataManager().get(traceData);
    }

    /**
     * 
     * clear the stored trace map.
     * 
     * @param traceData
     * @see #saveTraceMap(String, String, Map)
     */
    default void clearSavedTraceMap(String traceData) {
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
    default long cachedTraceMapLivingTimeInMilliSeconds() {
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
    default void passDataToSnippet(String currentStep, String renderTargetStep, Map<String, Object> traceMap) {
        T form = (T) traceMap.get(renderTargetStep);
        if (form == null) {
            form = (T) traceMap.get(currentStep);
            traceMap.put(renderTargetStep, form);
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

    default void passData(WebApplicationContext context, boolean byFlash, String key, Object data) {
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
    default boolean passDataToSnippetByFlash(String currentStep, String renderTargetStep, T form) {
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
    default CommonFormResult process(FormProcessData processData, T form) {
        return processValidation(processData, form);
    }

    /**
     * Sub classes can override this method to customize how to handle the validation result
     * 
     * @param form
     * @return
     */
    default CommonFormResult processValidation(FormProcessData processData, Object form) {
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
    default void outputValidationMessage(FormValidationMessage msg) {
        DefaultMessageRenderingHelper.getConfiguredInstance().err("#" + msg.getFieldName() + "-err-msg", msg.getMessage());
    }

    /**
     * 
     * Sub classes can override this method to supply customized validation mechanism.
     * 
     * @param form
     * @return
     */
    default List<FormValidationMessage> validate(Object form) {
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
    default FormValidator getTypeUnMatchValidator() {
        return new TypeUnMatchValidator();
    }

    /**
     * Sub classes can override this method to supply a customized value validator
     * 
     * @return
     */
    default FormValidator getValueValidator() {
        return new JsrValidator();
    }
}
