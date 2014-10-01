package com.astamuse.asta4d.web.form.field.impl;

import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;

public class RadioBoxPrepareRenderer extends AbstractRadioAndCheckBoxPrepareRenderer<RadioBoxPrepareRenderer> {

    public RadioBoxPrepareRenderer(AnnotatedPropertyInfo field) {
        super(field);
    }

    @SuppressWarnings("rawtypes")
    public RadioBoxPrepareRenderer(Class cls, String fieldName) {
        super(cls, fieldName);
    }

    /**
     * for test purpose, DO NOT USE IT!
     * 
     * @param fieldName
     */
    @Deprecated
    public RadioBoxPrepareRenderer(String fieldName) {
        super(fieldName);
    }

    @Override
    protected String getTypeString() {
        return "radio";
    }

}
