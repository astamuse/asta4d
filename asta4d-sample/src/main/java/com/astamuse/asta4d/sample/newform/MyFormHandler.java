package com.astamuse.asta4d.sample.newform;

import com.astamuse.asta4d.sample.handler.common.SinglePage2StepFormHandler;
import com.astamuse.asta4d.web.form.CommonFormResult;

public class MyFormHandler extends SinglePage2StepFormHandler<MyForm> {
    public MyFormHandler(Class<MyForm> formCls, String templatePrefix) {
        super(formCls, templatePrefix);
    }

    @Override
    protected CommonFormResult handle(String currentStep, MyForm form) {
        CommonFormResult result = super.handle(currentStep, form);
        if (result == CommonFormResult.SUCCESS && isConfirmStep(currentStep)) {
            try {
                // do update
                return CommonFormResult.SUCCESS;
            } catch (Exception ex) {
                return CommonFormResult.FAILED;
            }
        } else {
            return result;
        }

    }

}
