package com.astamuse.asta4d.web.form.renderer;

import com.astamuse.asta4d.render.Renderer;

public class InputBoxRenderer extends SimpleFormFieldValueRenderer {
    @Override
    public Renderer render(String nonNullString) {
        return Renderer.create("input", "value", nonNullString);
    }

}
