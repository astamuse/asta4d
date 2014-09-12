package com.astamuse.asta4d.web.form.flow.classical;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.flow.base.AbstractFormFlowHandler;
import com.astamuse.asta4d.web.form.flow.base.CommonFormResult;
import com.astamuse.asta4d.web.form.flow.base.FormFlowConstants;

public abstract class MultiStepFormFlowHandler<T> extends AbstractFormFlowHandler<T> {

    private static final Logger logger = LoggerFactory.getLogger(MultiStepFormFlowHandler.class);

    private String templatePrefix;

    public MultiStepFormFlowHandler(Class<T> formCls, String templatePrefix) {
        super(formCls);
        this.templatePrefix = templatePrefix;
    }

    protected abstract void updateForm(T form);

    protected boolean isConfirmStep(String step) {
        return ClassicalFormFlowConstant.STEP_CONFIRM.equalsIgnoreCase(step);
    }

    protected boolean isCompleteStep(String step) {
        return ClassicalFormFlowConstant.STEP_COMPLETE.equalsIgnoreCase(step);
    }

    protected String firstStepName() {
        return ClassicalFormFlowConstant.STEP_INPUT;
    }

    protected boolean treatCompleteStepAsExit() {
        return false;
    }

    @RequestHandler
    public String handle() throws Exception {
        return createTemplateFilePathForStep(handleWithRenderTargetStep());
    }

    protected String createTemplateFilePathForStep(String step) {
        if (isCompleteStep(step) && treatCompleteStepAsExit()) {
            return null;
        }

        if (FormFlowConstants.FORM_STEP_INIT_STEP.equals(step)) {
            step = firstStepName();
        }

        return templatePrefix + step + ".html";
    }

    @Override
    protected CommonFormResult handle(String currentStep, T form) {
        CommonFormResult result = super.handle(currentStep, form);
        if (result == CommonFormResult.SUCCESS && isConfirmStep(currentStep)) {
            try {
                updateForm(form);
                return CommonFormResult.SUCCESS;
            } catch (Exception ex) {
                logger.error("error occured on step:" + currentStep, ex);
                return CommonFormResult.FAILED;
            }
        } else {
            return result;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T retrieveFormInstance(Map<String, Object> traceMap, String currentStep) {
        if (isConfirmStep(currentStep)) {
            return (T) traceMap.get(currentStep);
        } else {
            return super.retrieveFormInstance(traceMap, currentStep);
        }
    }

    protected boolean passDataToSnippetByFlash(String currentStep, String renderTargetStep, T form, CommonFormResult result) {
        return ClassicalFormFlowConstant.STEP_COMPLETE.equals(renderTargetStep) && treatCompleteStepAsExit();
    }

    @Override
    protected void passDataToSnippet(String currentStep, String renderTargetStep, Map<String, Object> traceMap, CommonFormResult result) {
        // to confirm page
        if (isConfirmStep(renderTargetStep)) {
            Object form = traceMap.get(renderTargetStep);
            if (form == null) {
                traceMap.put(renderTargetStep, traceMap.get(currentStep));
            }
        }

        // from confirm to complete, which means the complete process has successfully completed.
        if (isConfirmStep(currentStep) && isCompleteStep(renderTargetStep)) {
            traceMap.put(renderTargetStep, traceMap.get(currentStep));
        }

        super.passDataToSnippet(currentStep, renderTargetStep, traceMap, result);
    }

}
