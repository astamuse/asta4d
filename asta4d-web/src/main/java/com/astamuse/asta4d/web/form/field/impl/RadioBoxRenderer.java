package com.astamuse.asta4d.web.form.field.impl;

import com.astamuse.asta4d.render.Renderer;

public class RadioBoxRenderer extends AbstractRadioAndCheckBoxRenderer {

    @Override
    public Renderer renderForDisplay(final String editTargetSelector, final String displayTargetSelector, final Object value) {
        if (value == null) {
            // for a null value, we need to cheat it as an array with one null element
            return super.renderForDisplay(editTargetSelector, displayTargetSelector, new Object[] { getNonNullString(null) });
        } else {
            return super.renderForDisplay(editTargetSelector, displayTargetSelector, value);
        }
    }
}
