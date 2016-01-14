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
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.CascadeArrayFunctions;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.flow.classical.ClassicalMultiStepFormFlowHandlerTrait;
import com.astamuse.asta4d.web.form.flow.classical.OneStepFormHandlerTrait;

/**
 * The basic mechanism of form flow. This interface is implemented as a template which allows developer to override any method for
 * customization.
 * <p>
 * To define a form flow, we need to plan a flow graph which describes how the flow flows.
 * <p>
 * (before first) --> step 1 <--> step2 <--> step3 <--> ... --> (exit)
 * <p>
 * Assume we have a flow as above, note that there can be cycles or branches, which means you can go any step from any other step in the
 * flow graph, what you need to do is to define how the step should be transfered.
 * 
 * <p>
 * 
 * The {@link FormProcessData} interface defined the basic step information and the default implementation {@link SimpleFormProcessData}
 * retrieves the step information from the submitted http query parameters which can be put into the HTML template files as a part of the
 * submitting form. However you can always decide how to retrieve the step information by implement your own {@link FormProcessData}.
 * 
 * <p>
 * 
 * The retrieved {@link FormProcessData} will suggest the following things:
 * <ul>
 * <li>step to exit
 * <li>step to back
 * <li>current step
 * <li>step for failing
 * <li>step for success
 * <li>flow trace id
 * </ul>
 * 
 * The default implementation of {@link #process()} will decide where the flow goes to by following sequence:
 * <ol>
 * <li>if step to exit does not empty, then exit the current flow by decide the render target step to be null
 * <li>if the current step is empty, then treat the current step as "before first" step
 * <li>if the current step "before first", then decide the render target step by {@link #firstStepName()}
 * <li>if the step to back is not empty, then decide the render target step to be the step to back.
 * <li>then calling {@link #processForm(FormProcessData, Object)} to process the submitted form data
 * <li>if the result of process is success, then decide the render target step to be the step for success, otherwise to be the step of
 * failing
 * </ol>
 * 
 * <i>See {@link #process()} for more details of what will be done.</i>
 * 
 * <p>
 * 
 * Then the following things is left for developers to decide as a rule of the flow:
 * <ul>
 * <li>override {@link #createTemplateFilePathForStep(String)} to decide how to convert a step to the corresponding target template file
 * path.
 * <li>override {@link #skipStoreTraceData(String, String, FormFlowTraceData)} to decide whether the flow trace data should be stored
 * <li>override {@link #passDataToSnippetByFlash(String, String, FormFlowTraceData)} to decide how to pass the form data for rendering to
 * snippet
 * </ul>
 * 
 * For most common situations, all the above things can be decided as general rules in the user project, so that a common parent class can
 * be utilized to perform the common assumption. There are two built-in flows representing the classical situations:
 * {@link OneStepFormHandlerTrait} and {@link ClassicalMultiStepFormFlowHandlerTrait}. Those two built-in interfaces can also be considered
 * as reference implementation of how to design and decide a form flow. User project is always recommended to extend from those two built-in
 * flows rather than this basic trait.
 * 
 * <p>
 * 
 * In user project, a common parent class is always recommended. A project limited common parent class can be used to decide the special
 * rules of the project and the following two method is strongly recommended to be overridden to return a configured validator.
 * <ul>
 * <li>{@link #getTypeUnMatchValidator()}
 * <li>{@link #getValueValidator()}
 * </ul>
 * 
 * 
 * @author e-ryu
 *
 * @param <T>
 * 
 * @see BasicFormFlowSnippetTrait
 * @see OneStepFormHandlerTrait
 * @see ClassicalMultiStepFormFlowHandlerTrait
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public interface BasicFormFlowHandlerTrait<T> extends CascadeArrayFunctions, FormFlowTraceDataAccessor, ValidationProcessor {

    public static final String FORM_PRE_DEFINED = "FORM_PRE_DEFINED#" + BasicFormFlowHandlerTrait.class.getName();

    public static final String FORM_EXTRA_DATA = "FORM_EXTRA_DATA#" + BasicFormFlowHandlerTrait.class.getName();

    public static final String PRE_INJECTION_TRACE_INFO = "PRE_INJECTION_TRACE_INFO#" + BasicFormFlowHandlerTrait.class.getName();

    /**
     * Sub classes must tell us the name of first step
     * 
     * @param step
     * @return
     */
    public String firstStepName();

    /**
     * translate a step to a target which may be a template file path usually, but a redirect target URL could be possible
     * 
     * @param step
     * @return target template file path
     */
    public String createMoveTargetForStep(String step);

    /**
     * Tells the form type of current flow.
     * 
     * @return
     */
    public Class<T> getFormCls();

    /**
     * Tells the concrete type of {@link FormProcessData}, default is {@link SimpleFormProcessData}.
     * 
     * @return
     */
    default Class<? extends FormProcessData> getFormProcessDataCls() {
        return SimpleFormProcessData.class;
    }

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
     * The default implementation as request handler which retrieve the process data and return converted the process result as target
     * template file path.
     * 
     * @return
     * @throws Exception
     */
    @RequestHandler
    default String handle() throws Exception {
        FormProcessData processData = (FormProcessData) InjectUtil.retrieveContextDataSetInstance(getFormProcessDataCls(),
                "not-exist-formProcessData", "");
        String targetStep = process(processData);
        return createMoveTargetForStep(targetStep);
    }

    /**
     * 
     * This method implement the basic mechanism which performs following things:
     * <ol>
     * <li>restore the trace data map which contains all the data in each step
     * <li>retrieve instance of target form data which type is specified by {@link #getFormCls()}
     * <li>if the current step is before first, set the render target step by {@link #firstStepName()}
     * <li>if the back step is not empty, then set the back step name to render target step
     * <li>else call {@link #processForm(FormProcessData, Object)} method to process the retrieved form data, currently in the process
     * method, only {@link #validateForm(FormProcessData, Object)} is invoked to perform validation.
     * <li>call {@link #rewriteTraceDataBeforeGoSnippet(String, String, FormFlowTraceData)} to rewrite trace data
     * <li>if {@link #skipStoreTraceData(String, String, FormFlowTraceData)} returns true, call {@link #clearStoredTraceData(String)} to
     * clear stored trace data, or call {@link #storeTraceData(String, String, String, FormFlowTraceData)} to store the trace data for next
     * step process
     * <li>call {@link #passDataToSnippet(String, String, Map)} to store all the retrieved and processed data for page rendering
     * <li>return the render target step name
     * </ol>
     * 
     * Sub classes could override this method to translate the returned target step to the actual render target template file path.
     * 
     * @return the render target step name
     * @throws Exception
     */
    default String process(FormProcessData processData) throws Exception {

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
            formResult = processForm(processData, form);
            if (formResult == CommonFormResult.SUCCESS) {
                renderTargetStep = processData.getStepSuccess();
            } else {
                renderTargetStep = processData.getStepFailed();
            }
        }

        rewriteTraceDataBeforeGoSnippet(currentStep, renderTargetStep, traceData);
        if (skipStoreTraceData(currentStep, renderTargetStep, traceData)) {
            clearStoredTraceData(traceId);
            traceId = "";
        } else {
            traceId = storeTraceData(currentStep, renderTargetStep, traceId, traceData);
        }
        passDataToSnippet(currentStep, renderTargetStep, traceId, traceData);

        return renderTargetStep;

    }

    /**
     * Whether the form flow should be exit when the target trace data is not found. The default is true.
     * 
     * @return
     */
    default boolean exitWhenTraceDataMissing() {
        return true;
    }

    /**
     * The default process will only call the {@link #validateForm(Object)} and sub classes can override this method to add extra process
     * logics such as updating form when validation succeeds.
     * 
     * @param processData
     * @param form
     * @return
     */
    default CommonFormResult processForm(FormProcessData processData, T form) {
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
                    } // end for loop

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
