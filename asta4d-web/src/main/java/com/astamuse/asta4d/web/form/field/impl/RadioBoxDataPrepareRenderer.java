package com.astamuse.asta4d.web.form.field.impl;

import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;

public class RadioBoxDataPrepareRenderer extends AbstractRadioAndCheckBoxDataPrepareRenderer<RadioBoxDataPrepareRenderer> {

    public RadioBoxDataPrepareRenderer(AnnotatedPropertyInfo field) {
        super(field);
    }

    @SuppressWarnings("rawtypes")
    public RadioBoxDataPrepareRenderer(Class cls, String fieldName) {
        super(cls, fieldName);
    }

    /**
     * for test purpose, DO NOT USE IT!
     * 
     * @param fieldName
     */
    @Deprecated
    public RadioBoxDataPrepareRenderer(String fieldName) {
        super(fieldName);
    }

    @Override
    protected String getTypeString() {
        return "radio";
    }

}
