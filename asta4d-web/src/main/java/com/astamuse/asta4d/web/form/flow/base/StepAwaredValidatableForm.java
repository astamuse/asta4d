package com.astamuse.asta4d.web.form.flow.base;

public interface StepAwaredValidatableForm {

    default Object getValidationTarget(String step) {
        return StepAwaredValidationFormHelper.getValidationTargetByAnnotation(this, step);
    }
}
