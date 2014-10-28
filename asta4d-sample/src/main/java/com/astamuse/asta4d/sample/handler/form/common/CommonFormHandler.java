package com.astamuse.asta4d.sample.handler.form.common;

import com.astamuse.asta4d.web.form.flow.classical.OneStepFormHandler;
import com.astamuse.asta4d.web.form.validation.FormValidator;

public abstract class CommonFormHandler<T> extends OneStepFormHandler<T> {

    // we will always add field name prefix to the error messages, so we use a field to store a pre generated instance rather than create it
    // at every time
    private SamplePrjTypeUnMatchValidator typeValidator = new SamplePrjTypeUnMatchValidator();

    // as the same as type validator, we cache the instance here
    private SamplePrjValueValidator valueValidator = new SamplePrjValueValidator();

    public CommonFormHandler(Class<T> formCls, String inputTemplateFile) {
        super(formCls, inputTemplateFile);
    }

    public CommonFormHandler(Class<T> formCls) {
        super(formCls);
    }

    @Override
    protected FormValidator getTypeUnMatchValidator() {
        return typeValidator;
    }

    @Override
    protected FormValidator getValueValidator() {
        return valueValidator;
    }

}
