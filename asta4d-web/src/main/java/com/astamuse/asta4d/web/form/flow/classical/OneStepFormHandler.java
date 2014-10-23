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

    /**
     * 
     * @return {@link CommonFormResult#SUCCESS}: final step of current form flow succeed and want to exit current form flow<br>
     *         {@link CommonFormResult#FAILED}: final step of current form flow succeed and want to forward to the first(initial) step of
     *         current form flow<br>
     *         <code>null</code>: there is nothing to do and want to go to the first step of current form flow
     * @throws Exception
     */
    @RequestHandler
    public CommonFormResult handle() throws Exception {
        CommonFormResult result = handleWithCommonFormResult();
        if (result == null) {
            return treatExitAs();
        } else {
            return result;
        }
    }

    protected CommonFormResult treatExitAs() {
        return CommonFormResult.SUCCESS;
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

    @Override
    protected boolean isCompleteStep(String step) {
        return ClassicalFormFlowConstant.STEP_COMPLETE.equalsIgnoreCase(step);
    }

    protected abstract void updateForm(T form);

}
