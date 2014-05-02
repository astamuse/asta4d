package com.astamuse.asta4d.web.form.renderer;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import com.astamuse.asta4d.render.Renderer;

public class SelectBoxRenderer extends SimpleFormFieldValueRenderer {

    @Override
    public Renderer render(String nonNullString) {
        Renderer renderer = Renderer.create("option", "selected", Clear);

        String selector = "option[value=" + nonNullString + "]";
        renderer.add(selector, "selected", "");
        return renderer;
    }

}
