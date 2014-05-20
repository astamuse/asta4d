package com.astamuse.asta4d.web.form.field;

import com.astamuse.asta4d.render.Renderer;

public class InputBoxRenderer extends SimpleFormFieldValueRenderer {
    @Override
    public Renderer renderForEdit(String nonNullString) {
        return Renderer.create("input", "value", nonNullString);
    }

}
