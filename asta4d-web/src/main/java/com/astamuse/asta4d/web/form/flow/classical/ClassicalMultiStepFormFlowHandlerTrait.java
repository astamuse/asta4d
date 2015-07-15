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
package com.astamuse.asta4d.web.form.flow.classical;

import java.util.Map;

import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.flow.base.BasicFormFlowHandlerTrait;
import com.astamuse.asta4d.web.form.flow.base.CommonFormResult;
import com.astamuse.asta4d.web.form.flow.base.FormFlowConstants;
import com.astamuse.asta4d.web.form.flow.base.FormFlowTraceData;
import com.astamuse.asta4d.web.form.flow.base.FormProcessData;

/**
 * This trait represents a classical flow that contains input/confirm/complete steps.
 * 
 * <p>
 * 
 * By default, this trait can handle a page with classical 3 steps: input, confirm and complete. If there are more than one input step, the
 * following methods can (for most cases, should) be overridden to customize the multiple steps:
 * <ul>
 * <li>{@link #generateFormInstanceFromContext(String)}
 * <li>{@link #processValidation(FormProcessData, Object)}
 * <li>{@link #rewriteTraceDataBeforeGoSnippet(String, String, FormFlowTraceData)}
 * <li>{@link #skipStoreTraceData(String, String, FormFlowTraceData)}
 * </ul>
 * 
 * Further methods can be overridden for more flexible flow definition. See details of the description of each method.
 * 
 * @author e-ryu
 *
 * @param <T>
 */
public interface ClassicalMultiStepFormFlowHandlerTrait<T> extends BasicFormFlowHandlerTrait<T> {

    /**
     * The default path var name of template files base path
     */
    public static final String VAR_TEMPLATE_BASE_PATH = "TEMPLATE_BASE_PATH#" + ClassicalMultiStepFormFlowHandlerTrait.class;

    /**
     * Sub classes must override this method to implement update logic.
     * 
     * @param form
     */
    public void updateForm(T form);

    /**
     * @see ClassicalFormFlowConstant#STEP_INPUT
     */
    @Override
    default String firstStepName() {
        return ClassicalFormFlowConstant.STEP_INPUT;
    }

    /**
     * @see ClassicalFormFlowConstant#STEP_CONFIRM
     */
    default String confirmStepName() {
        return ClassicalFormFlowConstant.STEP_CONFIRM;
    }

    /**
     * @see ClassicalFormFlowConstant#STEP_COMPLETE
     */
    default String completeStepName() {
        return ClassicalFormFlowConstant.STEP_COMPLETE;
    }

    /**
     * Sub classes should decide whether they want to show a complete page or simply exit current flow.
     * <p>
     * The default is false, which means the complete page will be shown always.
     * 
     * @return
     */
    default boolean treatCompleteStepAsExit() {
        return false;
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
     * In the parent class {@link BasicFormFlowHandlerTrait}'s implementation of saveTraceMap, it says that the sub class have the
     * responsibility to make sure save the trace map well, thus we override it to perform the obligation.
     * 
     * The trace map will not be saved when the flowing cases:
     * <ul>
     * <li>The form flow starts and the first step is shown as the entry of current flow
     * <li>The form flow is returned to the first step from the first step (usually validation failed).
     * <li>The form flow is returned to the first step from other step when the {@link #skipSaveTraceMapWhenBackedFromOtherStep()} returns
     * true
     * <li>The form flow has finished and is moving to the finish step.
     * </ul>
     * 
     * From parent:
     * <p>
     * {@inheritDoc}
     * 
     * @param currentStep
     * @param renderTargetStep
     * @param traceMap
     * @return
     */
    @Override
    default boolean skipStoreTraceData(String currentStep, String renderTargetStep, FormFlowTraceData traceData) {
        if (FormFlowConstants.FORM_STEP_BEFORE_FIRST.equals(currentStep)) {// init -> first
            // when the form flow start
            return true;
        } else if (firstStepName().equalsIgnoreCase(currentStep) && currentStep.equalsIgnoreCase(renderTargetStep)) {// first -> first
            // the form flow is stopped at the first step
            return true;
        } else if (firstStepName().equalsIgnoreCase(renderTargetStep)) { // first <- ?
            return true;
        } else if (completeStepName().equalsIgnoreCase(renderTargetStep)) {// ? -> complete
            // the form flow finished
            return true;
        } else {
            return false;
        }
    }

    /**
     * By default, the returned result from parent's handle will be translated to a target template file path.
     * 
     * <p>
     * 
     * From parent:
     * <p>
     * {@inheritDoc}
     * 
     * @see #createTemplateFilePathForStep(String)
     * @see #getTemplateBasePath()
     * @see #createTemplateFilePath(String, String)
     * 
     */
    @Override
    @RequestHandler
    default String handle() throws Exception {
        return createTemplateFilePathForStep(BasicFormFlowHandlerTrait.super.handle());
    }

    /**
     * Sub classes can override this method to customize how to translate a step to a target template file path.
     * 
     * @param step
     * @return
     */
    default String createTemplateFilePathForStep(String step) {
        if (step == null) {// exit flow
            return null;
        }
        if (completeStepName().equalsIgnoreCase(step) && treatCompleteStepAsExit()) {
            return null;
        }
        return createTemplateFilePath(getTemplateBasePath(), step);
    }

    /**
     * Sub class can override this method to return the base path of target template files. By default, it retrieves the value from pathvar
     * scope of context by key {@link #VAR_TEMPLATE_BASE_PATH}.
     * 
     * @return
     */
    default String getTemplateBasePath() {
        return Context.getCurrentThreadContext().getData(WebApplicationContext.SCOPE_PATHVAR, VAR_TEMPLATE_BASE_PATH);
    }

    /**
     * Sub class can override this method to customize how to create target template file path by given base path and step.
     * 
     * 
     * @param templateBasePath
     * @param step
     * @return
     */
    default String createTemplateFilePath(String templateBasePath, String step) {
        return templateBasePath + step + ".html";
    }

    /**
     * Whether we should call {@link #updateForm(Object)} when the {@link #processValidation(Object)} returns SUCCESS.
     * 
     * @param processData
     * @return
     */
    default boolean doUpdateOnValidationSuccess(FormProcessData processData) {
        return confirmStepName().equalsIgnoreCase(processData.getStepCurrent()) &&
                completeStepName().equalsIgnoreCase(processData.getStepSuccess());
    }

    /**
     * 
     * This method will call {@link #updateForm(Object)} when the validation is success and the
     * {@link #doUpdateOnValidationSuccess(FormProcessData)} returns true.
     * 
     * <p>
     * 
     * From parent:
     * <p>
     * {@inheritDoc}
     * 
     */
    @Override
    default CommonFormResult process(FormProcessData processData, T form) {
        CommonFormResult result = processValidation(processData, form);
        if (result == CommonFormResult.SUCCESS && doUpdateOnValidationSuccess(processData)) {
            try {
                updateForm(form);
                return CommonFormResult.SUCCESS;
            } catch (Exception ex) {
                LoggerFactory.getLogger(this.getClass()).error("error occured on step:" + processData.getStepCurrent(), ex);
                return CommonFormResult.FAILED;
            }
        } else {
            return result;
        }
    }

    /**
     * This method defines the default policy of how to retrieve the form instance for current step. For confirm/complete step, we will
     * always retrieve the form instance from the traceMap which have stored the form instance for current step at last step (when
     * {@link #passDataToSnippet(String, String, Map)} was invoked), for other steps, the parent's implementation will be invoked.
     * 
     * <p>
     * From parent:
     * <p>
     * {@inheritDoc}
     * 
     */
    @SuppressWarnings("unchecked")
    @Override
    default T retrieveFormInstance(FormFlowTraceData traceData, String currentStep) {
        // for confirm and complete step, the form saved at last step would be used.
        if (confirmStepName().equalsIgnoreCase(currentStep) || completeStepName().equalsIgnoreCase(currentStep)) {
            return (T) traceData.getStepFormMap().get(currentStep);
        } else {
            return BasicFormFlowHandlerTrait.super.retrieveFormInstance(traceData, currentStep);
        }
    }

    /**
     * When the form flow finished, if the complete page rendering is skipped({@link #treatCompleteStepAsExit()} returns true), we have to
     * pass data to snippet via flash scope since we have to exit the current flow and the current request will be redirect by a 302
     * response.
     * 
     * <p>
     * From parent:
     * <p>
     * {@inheritDoc}
     * 
     * @return true when step is complete and {@link #treatCompleteStepAsExit()} returns true
     */
    @Override
    default boolean passDataToSnippetByFlash(String currentStep, String renderTargetStep, FormFlowTraceData traceData) {
        return completeStepName().equals(renderTargetStep) && treatCompleteStepAsExit();
    }
}
