package com.astamuse.asta4d.web.form.field.impl;

import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;

public class CheckBoxPrepareRenderer extends AbstractRadioAndCheckBoxPrepareRenderer<CheckBoxPrepareRenderer> {

    public CheckBoxPrepareRenderer(@SuppressWarnings("rawtypes") Class cls, String fieldName) {
        super(cls, fieldName);
    }

    public CheckBoxPrepareRenderer(AnnotatedPropertyInfo field) {
        super(field);
    }

    /**
     * For test purpose, DO NOT USE IT!
     * 
     * @param fieldName
     */
    @Deprecated
    public CheckBoxPrepareRenderer(String fieldName) {
        super(fieldName);
    }

    @Override
    protected String getTypeString() {
        return "checkbox";
    }

}
