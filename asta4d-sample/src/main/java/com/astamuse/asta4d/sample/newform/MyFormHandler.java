package com.astamuse.asta4d.sample.newform;

import com.astamuse.asta4d.sample.handler.form.PersonForm;
import com.astamuse.asta4d.web.form.flow.base.CommonFormResult;
import com.astamuse.asta4d.web.form.flow.common.ClassicalFormFlowHandler;

public class MyFormHandler extends ClassicalFormFlowHandler<PersonForm> {
    public MyFormHandler(Class<PersonForm> formCls, String templatePrefix) {
        super(formCls, templatePrefix);
    }

    @Override
    protected CommonFormResult handle(String currentStep, PersonForm form) {
        CommonFormResult result = super.handle(currentStep, form);
        if (result == CommonFormResult.SUCCESS && isConfirmStep(currentStep)) {
            try {
                // do update here
                return CommonFormResult.SUCCESS;
            } catch (Exception ex) {
                return CommonFormResult.FAILED;
            }
        } else {
            return result;
        }

    }

}
