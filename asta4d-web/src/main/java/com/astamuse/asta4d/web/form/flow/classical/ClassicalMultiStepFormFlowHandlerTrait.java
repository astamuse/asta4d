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
import com.astamuse.asta4d.web.form.flow.base.FormProcessData;

public interface ClassicalMultiStepFormFlowHandlerTrait<T> extends BasicFormFlowHandlerTrait<T> {

    public static final String VAR_TEMPLATE_BASE_PATH = "TEMPLATE_BASE_PATH#" + ClassicalMultiStepFormFlowHandlerTrait.class;

    default String getTemplateBasePath() {
        return Context.getCurrentThreadContext().getData(WebApplicationContext.SCOPE_PATHVAR, VAR_TEMPLATE_BASE_PATH);
    }

    /**
     * Sub classes must override this method to implement update logic.
     * 
     * @param form
     */
    public void updateForm(T form);

    @Override
    default String firstStepName() {
        return ClassicalFormFlowConstant.STEP_INPUT;
    }

    @Override
    default String completeStepName() {
        return ClassicalFormFlowConstant.STEP_COMPLETE;
    }

    default String confirmStepName() {
        return ClassicalFormFlowConstant.STEP_CONFIRM;
    }

    @Override
    default boolean removeCurrentStepDataFromTraceMapWhenStepBack(String currentStep, String renderTargetStep) {
        // remove saved confirm step data when back from confirm step
        return confirmStepName().equalsIgnoreCase(currentStep);
    }

    /**
     * In the parent class {@link AbstractFormFlowHandler}'s implementation of saveTraceMap, it says that the sub class have the
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
     * @param currentStep
     * @param renderTargetStep
     * @param traceMap
     * @return
     */
    @Override
    default boolean skipSaveTraceMap(String currentStep, String renderTargetStep, Map<String, Object> traceMap) {
        if (FormFlowConstants.FORM_STEP_BEFORE_FIRST.equals(currentStep)) {
            // when the form flow start
            return true;
        } else if (firstStepName().equalsIgnoreCase(currentStep) && currentStep.equalsIgnoreCase(renderTargetStep)) {
            // the form flow is stopped at the first step
            return true;
        } else if (firstStepName().equalsIgnoreCase(renderTargetStep)) {
            // the form flow is returned to the first step
            return skipSaveTraceMapWhenBackedFromOtherStep();
        } else if (completeStepName().equalsIgnoreCase(renderTargetStep)) {
            // the form flow finished
            return true;
        } else {
            return false;
        }
    }

    /**
     * For most cases, we return true by default to tell the form flow skip saving trace map when we returned to the first step in spite of
     * any other concerns.
     * 
     * @return
     */
    default boolean skipSaveTraceMapWhenBackedFromOtherStep() {
        return true;
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

    @SuppressWarnings("unchecked")
    @Override
    default T retrieveFormInstance(Map<String, Object> traceMap, String currentStep) {
        // for confirm and complete step, the form saved at last step would be used.
        if (confirmStepName().equalsIgnoreCase(currentStep) || completeStepName().equalsIgnoreCase(currentStep)) {
            return (T) traceMap.get(currentStep);
        } else {
            return BasicFormFlowHandlerTrait.super.retrieveFormInstance(traceMap, currentStep);
        }
    }

    /**
     * When the form flow finished, if the complete page rendering is skipped({@link #treatCompleteStepAsExit()} returns true), we have to
     * pass data to snippet via flash scope since we have to exit the current flow and the current request will be redirect by a 302
     * response.
     * 
     * @return true when step is complete and {@link #treatCompleteStepAsExit()} returns true
     */
    @Override
    default boolean passDataToSnippetByFlash(String currentStep, String renderTargetStep, T form) {
        return completeStepName().equals(renderTargetStep) && treatCompleteStepAsExit();
    }
}
