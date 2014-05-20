package com.astamuse.asta4d.web.form;

import com.astamuse.asta4d.render.Renderer;

public interface FormFieldValueRenderer {
    public Renderer renderForEdit(String editTargetSelector, Object value);

    public Renderer renderForDisplay(String editTargetSelector, String displayTargetSelector, Object value);
}
