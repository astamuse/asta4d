package com.astamuse.asta4d.web.form.renderer;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.web.form.FormFieldValueRenderer;

public abstract class SimpleFormFieldValueRenderer implements FormFieldValueRenderer {

    @Override
    public final Renderer render(String targetSelector, Object value) {
        String v = value == null ? "" : value.toString();
        if (v == null) {
            v = "";
        }

        return Renderer.create(targetSelector, render(v));
    }

    public abstract Renderer render(String nonNullString);

}
