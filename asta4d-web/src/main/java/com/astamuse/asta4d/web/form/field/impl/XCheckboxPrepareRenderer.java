package com.astamuse.asta4d.web.form.field.impl;

import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;

public class CheckboxPrepareRenderer extends AbstractRadioAndCheckboxPrepareRenderer<CheckboxPrepareRenderer> {

    public CheckboxPrepareRenderer(@SuppressWarnings("rawtypes") Class cls, String fieldName) {
        super(cls, fieldName);
    }

    public CheckboxPrepareRenderer(AnnotatedPropertyInfo field) {
        super(field);
    }

    /**
     * For test purpose, DO NOT USE IT!
     * 
     * @param fieldName
     */
    @Deprecated
    public CheckboxPrepareRenderer(String fieldName) {
        super(fieldName);
    }

    @Override
    protected String getTypeString() {
        return "checkbox";
    }

}
