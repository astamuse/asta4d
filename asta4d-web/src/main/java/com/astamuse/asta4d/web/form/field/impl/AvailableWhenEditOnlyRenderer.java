package com.astamuse.asta4d.web.form.field.impl;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.web.form.field.FormFieldValueRenderer;

public class AvailableWhenEditOnlyRenderer implements FormFieldValueRenderer {

    @Override
    public Renderer renderForEdit(String editTargetSelector, Object value) {
        return Renderer.create();
    }

    @Override
    public Renderer renderForDisplay(String editTargetSelector, String displayTargetSelector, Object value) {
        Renderer render = Renderer.create().disableMissingSelectorWarning();
        render.add(editTargetSelector, Clear).add(displayTargetSelector, Clear);
        return render.enableMissingSelectorWarning();
    }

}
