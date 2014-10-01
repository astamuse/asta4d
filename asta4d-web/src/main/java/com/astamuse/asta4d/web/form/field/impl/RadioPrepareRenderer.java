package com.astamuse.asta4d.web.form.field.impl;

import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;

public class RadioPrepareRenderer extends AbstractRadioAndCheckboxPrepareRenderer<RadioPrepareRenderer> {

    public RadioPrepareRenderer(AnnotatedPropertyInfo field) {
        super(field);
    }

    @SuppressWarnings("rawtypes")
    public RadioPrepareRenderer(Class cls, String fieldName) {
        super(cls, fieldName);
    }

    /**
     * for test purpose, DO NOT USE IT!
     * 
     * @param fieldName
     */
    @Deprecated
    public RadioPrepareRenderer(String fieldName) {
        super(fieldName);
    }

    @Override
    protected String getTypeString() {
        return "radio";
    }

}
