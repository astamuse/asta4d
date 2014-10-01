package com.astamuse.asta4d.web.form.field;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;

public abstract class SimpleFormFieldPrepareRenderer implements FormFieldPrepareRenderer {

    private AnnotatedPropertyInfo field;

    /**
     * for test purpose
     */
    @SuppressWarnings("unused")
    private String givenFieldName;

    public SimpleFormFieldPrepareRenderer(AnnotatedPropertyInfo field) {
        this.field = field;
    }

    @SuppressWarnings("rawtypes")
    public SimpleFormFieldPrepareRenderer(Class cls, String fieldName) {
        this(AnnotatedPropertyUtil.retrievePropertyByName(cls, fieldName));
    }

    /**
     * this constructor is for test purpose, DO NOT USE IT!!!
     * 
     * @param fieldName
     */
    @Deprecated
    public SimpleFormFieldPrepareRenderer(String fieldName) {
        givenFieldName = fieldName;
    }

    /**
     * this method is for test purpose, DO NOT USE IT!!!
     * 
     * @return
     */
    @Deprecated
    public String getGivenFieldName() {
        return givenFieldName;
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
