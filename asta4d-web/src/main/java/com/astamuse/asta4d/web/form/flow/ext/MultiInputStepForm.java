package com.astamuse.asta4d.web.form.flow.ext;

import com.astamuse.asta4d.web.form.flow.base.ValidationStepAwaredForm;

public interface MultiInputStepForm extends ValidationStepAwaredForm {

    public Object getSubInputFormByStep(String step);

    public void setSubInputFormForStep(String step, Object subForm);

    @Override
    default Object getValidationTarget(String step) {
        Object validateObj = getSubInputFormByStep(step);
        return validateObj == null ? this : validateObj;
    }
}
