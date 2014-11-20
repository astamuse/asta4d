package com.astamuse.asta4d.web.form.flow.classical;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.flow.base.AbstractFormFlowHandler;
import com.astamuse.asta4d.web.form.flow.base.CommonFormResult;
import com.astamuse.asta4d.web.form.flow.base.FormFlowConstants;
import com.astamuse.asta4d.web.form.flow.base.FormProcessData;

public abstract class MultiStepFormFlowHandler<T> extends AbstractFormFlowHandler<T> {

    private static final Logger logger = LoggerFactory.getLogger(MultiStepFormFlowHandler.class);

    public static final String VAR_TEMPLATE_BASE_PATH = "TEMPLATE_BASE_PATH#" + MultiStepFormFlowHandler.class;

    private String templateBasePath;

    public MultiStepFormFlowHandler(Class<T> formCls, String templateBasePath) {
        super(formCls);
        this.templateBasePath = templateBasePath;
    }

    public MultiStepFormFlowHandler(Class<T> formCls) {
        super(formCls);
        this.templateBasePath = null;
    }

    /**
     * Sub classes must override this method to implement update logic.
     * 
     * @param form
     */
    protected abstract void updateForm(T form);

    @Override
    protected String firstStepName() {
        return ClassicalFormFlowConstant.STEP_INPUT;
    }

    @Override
    protected String completeStepName() {
        return ClassicalFormFlowConstant.STEP_COMPLETE;
    }

    protected String confirmStepName() {
        return ClassicalFormFlowConstant.STEP_CONFIRM;
    }

    @Override
    protected boolean removeCurrentStepDataFromTraceMapWhenStepBack(String currentStep, String renderTargetStep) {
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
    protected boolean skipSaveTraceMap(String currentStep, String renderTargetStep, Map<String, Object> traceMap) {
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
    protected boolean skipSaveTraceMapWhenBackedFromOtherStep() {
        return true;
    }

    /**
     * Sub classes should decide whether they want to show a complete page or simply exit current flow.
     * <p>
     * The default is false, which means the complete page will be shown always.
     * 
     * @return
     */
    protected boolean treatCompleteStepAsExit() {
        return false;
    }

    @RequestHandler
    public String handle() throws Exception {
        return createTemplateFilePathForStep(super.handle());
    }

    /**
     * Sub classes can override this method to customize how to translate a step to a target template file path.
     * 
     * @param step
     * @return
     */
    protected String createTemplateFilePathForStep(String step) {
        if (step == null) {// exit flow
            return null;
        }

        if (completeStepName().equalsIgnoreCase(step) && treatCompleteStepAsExit()) {
            return null;
        }

        if (templateBasePath == null) {
            String varPath = Context.getCurrentThreadContext().getData(WebApplicationContext.SCOPE_PATHVAR, VAR_TEMPLATE_BASE_PATH);
            return createTemplateFilePath(varPath, step);
        } else {
            return createTemplateFilePath(templateBasePath, step);
        }
    }

    protected String createTemplateFilePath(String templateBasePath, String step) {
        return templateBasePath + step + ".html";
    }

    /**
     * Whether we should call {@link #updateForm(Object)} when the {@link #processValidation(Object)} returns SUCCESS.
     * 
     * @param processData
     * @return
     */
    protected boolean doUpdateOnValidationSuccess(FormProcessData processData) {
        return confirmStepName().equalsIgnoreCase(processData.getStepCurrent()) &&
                completeStepName().equalsIgnoreCase(processData.getStepSuccess());
    }

    @Override
    protected CommonFormResult process(FormProcessData processData, T form) {
        CommonFormResult result = processValidation(processData, form);
        if (result == CommonFormResult.SUCCESS && doUpdateOnValidationSuccess(processData)) {
            try {
                updateForm(form);
                return CommonFormResult.SUCCESS;
            } catch (Exception ex) {
                logger.error("error occured on step:" + processData.getStepCurrent(), ex);
                return CommonFormResult.FAILED;
            }
        } else {
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T retrieveFormInstance(Map<String, Object> traceMap, String currentStep) {
        // for confirm and complete step, the form saved at last step would be used.
        if (confirmStepName().equalsIgnoreCase(currentStep) || completeStepName().equalsIgnoreCase(currentStep)) {
            return (T) traceMap.get(currentStep);
        } else {
            return super.retrieveFormInstance(traceMap, currentStep);
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
    protected boolean passDataToSnippetByFlash(String currentStep, String renderTargetStep, T form) {
        return completeStepName().equals(renderTargetStep) && treatCompleteStepAsExit();
    }

}
