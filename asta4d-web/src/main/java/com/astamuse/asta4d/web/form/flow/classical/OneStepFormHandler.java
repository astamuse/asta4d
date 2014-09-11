package com.astamuse.asta4d.web.form.flow.classical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.flow.base.AbstractFormFlowHandler;
import com.astamuse.asta4d.web.form.flow.base.CommonFormResult;

public abstract class OneStepFormHandler<T> extends AbstractFormFlowHandler<T> {

    private static final Logger logger = LoggerFactory.getLogger(OneStepFormHandler.class);

    public OneStepFormHandler(Class<T> formCls) {
        super(formCls);
    }

    @RequestHandler
    public CommonFormResult handle() throws Exception {
        return handleWithCommonFormResult();
    }

    @Override
    protected CommonFormResult handle(String currentStep, T form) {
        CommonFormResult result = super.handle(currentStep, form);
        if (result == CommonFormResult.SUCCESS) {
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

    protected boolean passDataToSnippetByFlash(String currentStep, String renderTargetStep, T form, CommonFormResult result) {
        return ClassicalFormFlowConstant.STEP_COMPLETE.equals(renderTargetStep);
    }

    protected abstract void updateForm(T form);

}
