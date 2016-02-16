package com.astamuse.asta4d.web.form.flow.ext;

import java.util.Map;

import com.astamuse.asta4d.web.form.flow.base.StepAwaredValidatableForm;

public interface MultiInputStepForm extends StepAwaredValidatableForm {

    public void mergeInputDataForConfirm(Map<String, Object> inputForms);

}
