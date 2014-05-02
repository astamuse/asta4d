package com.astamuse.asta4d.web.form;

import com.astamuse.asta4d.render.Renderer;

public interface FormFieldValueRenderer {
    public Renderer render(String targetSelector, Object value);
}
