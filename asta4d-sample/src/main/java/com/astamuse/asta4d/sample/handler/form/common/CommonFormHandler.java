package com.astamuse.asta4d.sample.handler.form.common;

import com.astamuse.asta4d.web.form.flow.classical.OneStepFormHandler;
import com.astamuse.asta4d.web.form.validation.FormValidator;

// @ShowCode:showCommonFormHandlerStart
/**
 * A common parent handler to configure the common actions of form flow process in application. <br>
 * For quick start, an empty class body would be good enough. You only need to do the customization when you really need to do it!!!
 * 
 */
public abstract class CommonFormHandler<T> extends OneStepFormHandler<T> {

    // we use a field to store a pre generated instance rather than create it at every time
    private SamplePrjTypeUnMatchValidator typeValidator = new SamplePrjTypeUnMatchValidator(false);

    // as the same as type validator, we cache the value validator instance here
    private SamplePrjValueValidator valueValidator = new SamplePrjValueValidator(false);

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
// @ShowCode:showCommonFormHandlerEnd