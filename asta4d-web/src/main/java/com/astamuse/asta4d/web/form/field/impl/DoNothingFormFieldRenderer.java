package com.astamuse.asta4d.web.form.field.impl;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.web.form.field.FormFieldValueRenderer;

public class DoNothingFormFieldRenderer implements FormFieldValueRenderer {

    @Override
    public Renderer renderForEdit(String editTargetSelector, Object value) {
        return Renderer.create();
    }

    @Override
    public Renderer renderForDisplay(String editTargetSelector, String displayTargetSelector, Object value) {
        return Renderer.create();
    }

}
