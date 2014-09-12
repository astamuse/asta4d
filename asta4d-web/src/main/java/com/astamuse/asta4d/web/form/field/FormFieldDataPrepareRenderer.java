package com.astamuse.asta4d.web.form.field;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.web.form.annotation.FormField;

public interface FormFieldDataPrepareRenderer {

    public AnnotatedPropertyInfo<FormField> targetField();

    public Renderer preRender(String editSelector, String displaySelector);

    public Renderer postRender(String editSelector, String displaySelector);
}
