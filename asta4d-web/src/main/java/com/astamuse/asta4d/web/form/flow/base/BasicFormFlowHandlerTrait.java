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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.InjectTrace;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.RedirectInterceptor;
import com.astamuse.asta4d.web.dispatch.RedirectUtil;
import com.astamuse.asta4d.web.form.CascadeArrayFunctions;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;

/**
 * The basic mechanism of form flow. This interface is implemented as a template which allows developer to override any method for
 * customization. See details at {@link #handle()}.
 * 
 * @author e-ryu
 *
 * @param <T>
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public interface BasicFormFlowHandlerTrait<T> extends CascadeArrayFunctions, FormFlowTraceDataAccessor, ValidationProcessor {

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
     * 
     * This method implement the basic mechanism which performs following things:
     * <ol>
     * <li>retrieve instance of {@link FormProcessData} from context.
     * <li>restore the trace data map which contains all the data in each step
     * <li>retrieve instance of target form data which type is specified by {@link #getFormCls()}
     * <li>if the target step is not back to previous, call {@link #process(FormProcessData, Object)} method to process the retrieved form
     * data, currently in the process method, only {@link #processValidation(FormProcessData, Object)} is invoked to perform validation.
     * <li>call {@link #passDataToSnippet(String, String, Map)} to store all the retrieved and processed data for page rendering
     * <li>
     * </ol>
     * 
     * Sub classes could override this method to translate the returned target step to the actual render target template file path.
     * 
     * @return the render target step name
     * @throws Exception
     */
    default String handle() throws Exception {
        FormProcessData processData = (FormProcessData) InjectUtil.retrieveContextDataSetInstance(getFormProcessDataCls(),
                "not-exist-formProcessData", "");

        String traceId = processData.getFlowTraceId();

        // clear trace data when exit
        if (processData.getStepExit() != null) {
            clearStoredTraceData(traceId);
            return null;
        }

        String currentStep = processData.getStepCurrent();

        FormFlowTraceData traceData;
        if (StringUtils.isEmpty(traceId)) {
            traceData = createEmptyTraceData();
        } else {
            traceData = retrieveTraceData(traceId);
            if (traceData == null) {
                if (exitWhenTraceDataMissing()) {
                    return null;
                } else {
                    traceId = "";
                    traceData = createEmptyTraceData();
                    currentStep = null;
                }
            }
        }

        // the first time access without existing input data or saved tracemap could not be retrieved(usually due to timeout)
        if (currentStep == null) {
            currentStep = FormFlowConstants.FORM_STEP_BEFORE_FIRST;
            // save init form as predefined form
            Context.getCurrentThreadContext().setData(FORM_PRE_DEFINED, createInitForm());
        }

        T form = retrieveFormInstance(traceData, currentStep);
        traceData.getStepFormMap().put(currentStep, form);

        String renderTargetStep = null;
        CommonFormResult formResult = null;

        if (FormFlowConstants.FORM_STEP_BEFORE_FIRST.equals(currentStep)) {
            renderTargetStep = firstStepName();
        } else if (processData.getStepBack() != null) {
            renderTargetStep = processData.getStepBack();
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

        rewriteTraceDataBeforeGoSnippet(currentStep, renderTargetStep, traceData);
        if (completeStepName().equalsIgnoreCase(renderTargetStep) || skipStoreTraceData(currentStep, renderTargetStep, traceData)) {
            clearStoredTraceData(traceId);
            traceId = "";
        } else {
            traceId = storeTraceData(currentStep, renderTargetStep, traceId, traceData);
        }
        passDataToSnippet(currentStep, renderTargetStep, traceId, traceData);

        return renderTargetStep;

    }

    default boolean exitWhenTraceDataMissing() {
        return true;
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
     * Sub classes can override this method to customize how to retrieve the form instance by step information. By default, a form instance
     * will be retrieved from context by calling {@link #generateFormInstanceFromContext(String)}.
     * 
     * @param traceMap
     * @param currentStep
     * @return
     */
    default T retrieveFormInstance(FormFlowTraceData traceData, String currentStep) {
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
     * Always override render target step form data by current step form data
     * 
     * @param currentStep
     * @param renderTargetStep
     * @param traceData
     */
    default void rewriteTraceDataBeforeGoSnippet(String currentStep, String renderTargetStep, FormFlowTraceData traceData) {
        Map<String, Object> formMap = traceData.getStepFormMap();
        formMap.put(renderTargetStep, formMap.get(currentStep));
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
    default void passDataToSnippet(String currentStep, String renderTargetStep, String traceId, FormFlowTraceData traceData) {
        WebApplicationContext context = WebApplicationContext.getCurrentThreadWebApplicationContext();

        boolean byFlash = passDataToSnippetByFlash(currentStep, renderTargetStep, traceData);

        passData(context, byFlash, FormFlowConstants.FORM_FLOW_TRACE_ID, traceId);
        passData(context, byFlash, FormFlowConstants.FORM_FLOW_TRACE_DATA, traceData);
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
    default boolean passDataToSnippetByFlash(String currentStep, String renderTargetStep, FormFlowTraceData traceData) {
        return false;
    }

}
