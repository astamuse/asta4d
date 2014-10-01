package com.astamuse.asta4d.web.form.field.impl;

import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;

public class CheckBoxDataPrepareRenderer extends AbstractRadioAndCheckBoxDataPrepareRenderer<CheckBoxDataPrepareRenderer> {

    public CheckBoxDataPrepareRenderer(@SuppressWarnings("rawtypes") Class cls, String fieldName) {
        super(cls, fieldName);
    }

    public CheckBoxDataPrepareRenderer(AnnotatedPropertyInfo field) {
        super(field);
    }

    /**
     * For test purpose, DO NOT USE IT!
     * 
     * @param fieldName
     */
    @Deprecated
    public CheckBoxDataPrepareRenderer(String fieldName) {
        super(fieldName);
    }

    @Override
    protected String getTypeString() {
        return "checkbox";
    }

}
