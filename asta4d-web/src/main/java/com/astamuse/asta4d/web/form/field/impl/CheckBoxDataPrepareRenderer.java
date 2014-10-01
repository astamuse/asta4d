package com.astamuse.asta4d.web.form.field.impl;

import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;

public class CheckBoxDataPrepareRenderer extends RadioBoxDataPrepareRenderer {

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
    @SuppressWarnings("deprecation")
    @Deprecated
    public CheckBoxDataPrepareRenderer(String fieldName) {
        super(fieldName);
    }

}
