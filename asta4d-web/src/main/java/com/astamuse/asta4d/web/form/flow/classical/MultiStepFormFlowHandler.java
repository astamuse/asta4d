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

    protected abstract void updateForm(T form);

    protected boolean doUpdateOnSuccess(String step) {
        return ClassicalFormFlowConstant.STEP_CONFIRM.equalsIgnoreCase(step);
    }

    protected boolean isConfirmStep(String step) {
        return ClassicalFormFlowConstant.STEP_CONFIRM.equalsIgnoreCase(step);
    }

    @Override
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
        return createTemplateFilePathForStep(super.handle());
    }

    protected String createTemplateFilePathForStep(String step) {
        if (step == null) {// exit flow
            return null;
        }

        if (isCompleteStep(step)) {
            if (treatCompleteStepAsExit()) {
                return null;
            }
        }

        if (FormFlowConstants.FORM_STEP_INIT_STEP.equals(step)) {
            step = firstStepName();
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

    @Override
    protected CommonFormResult handle(String currentStep, T form) {
        CommonFormResult result = super.handle(currentStep, form);
        if (result == CommonFormResult.SUCCESS && doUpdateOnSuccess(currentStep)) {
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
