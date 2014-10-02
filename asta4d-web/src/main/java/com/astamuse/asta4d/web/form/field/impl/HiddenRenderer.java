package com.astamuse.asta4d.web.form.field.impl;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.web.form.field.SimpleFormFieldValueRenderer;

public class HiddenRenderer extends SimpleFormFieldValueRenderer {
    @Override
    public Renderer renderForEdit(String nonNullString) {
        return Renderer.create("input", "value", nonNullString);
    }

    @Override
    protected Renderer renderForDisplay(String editTargetSelector, String displayTargetSelector, String nonNullString) {
        return Renderer.create();
    }
}
