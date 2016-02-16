package com.astamuse.asta4d.web.form.flow.ext;

import com.astamuse.asta4d.web.form.flow.base.StepAwaredValidatableForm;

public interface MultiInputStepForm extends StepAwaredValidatableForm {

    public void mergeInputDataForConfirm(String step, Object inputForm);

}
