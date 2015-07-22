package com.astamuse.asta4d.web.form.flow.ext;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.web.form.flow.base.FormRenderingData;

@ContextDataSet
public class MultiInputStepFormRenderingData extends FormRenderingData {

    @ContextData(scope = Context.SCOPE_ATTR, name = "form-step")
    private String formStep;

    public String getFormStep() {
        return formStep;
    }

}
