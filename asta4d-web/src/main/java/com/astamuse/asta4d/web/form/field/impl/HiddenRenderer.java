package com.astamuse.asta4d.web.form.field.impl;

import com.astamuse.asta4d.render.Renderer;

public class HiddenRenderer extends InputDefaultRenderer {

    @Override
    protected Renderer renderForDisplay(String editTargetSelector, String displayTargetSelector, String nonNullString) {
        return Renderer.create();
    }
}
