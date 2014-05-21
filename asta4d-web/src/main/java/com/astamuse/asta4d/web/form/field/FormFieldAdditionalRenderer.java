package com.astamuse.asta4d.web.form.field;

import java.lang.reflect.Field;

import com.astamuse.asta4d.render.Renderer;

public interface FormFieldAdditionalRenderer {

    public Field targetField();

    public Renderer preRender(String editSelector, String displaySelector);

    public Renderer postRender(String editSelector, String displaySelector);
}
