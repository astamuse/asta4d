package com.astamuse.asta4d.web.form.flow;

import java.util.Map;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.flow.common.CommonFormResult;
import com.astamuse.asta4d.web.form.flow.common.AbstractFlowFormHandler;

public class SinglePage2StepFormHandler<T> extends AbstractFlowFormHandler<T> {

    private String templatePrefix;

    public SinglePage2StepFormHandler(Class<T> formCls, String templatePrefix) {
        super(formCls);
        this.templatePrefix = templatePrefix;
    }

    protected boolean isConfirmStep(String step) {
        return "confirm".equalsIgnoreCase(step);
    }

    protected boolean isCompleteStep(String step) {
        return "complete".equalsIgnoreCase(step);
    }

    @RequestHandler
    public String handle() throws Exception {
        return createTemplateFilePathForStep(handleWithRenderTargetResult());
    }

    protected String createTemplateFilePathForStep(String step) {
        return templatePrefix + step + ".html";
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
