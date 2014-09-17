package com.astamuse.asta4d.web.form.field;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;

public abstract class SimpleFormFieldAdditionalRenderer implements FormFieldDataPrepareRenderer {

    private AnnotatedPropertyInfo field;

    public SimpleFormFieldAdditionalRenderer(AnnotatedPropertyInfo field) {
        this.field = field;
    }

    @SuppressWarnings("rawtypes")
    public SimpleFormFieldAdditionalRenderer(Class cls, String fieldName) {
        this(AnnotatedPropertyUtil.retrievePropertyByName(cls, fieldName));
    }

    @Override
    public AnnotatedPropertyInfo targetField() {
        return field;
    }

    @Override
    public Renderer preRender(String editSelector, String displaySelector) {
        return Renderer.create();
    }

    @Override
    public Renderer postRender(String editSelector, String displaySelector) {
        return Renderer.create();
    }

}
