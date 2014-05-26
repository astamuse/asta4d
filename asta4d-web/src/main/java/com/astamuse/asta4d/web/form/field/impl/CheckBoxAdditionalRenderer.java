package com.astamuse.asta4d.web.form.field.impl;

import java.lang.reflect.Field;

public class CheckBoxAdditionalRenderer extends RadioBoxAdditionalRenderer {

    public CheckBoxAdditionalRenderer(Class cls, String fieldName) {
        super(cls, fieldName);
    }

    public CheckBoxAdditionalRenderer(Field field) {
        super(field);
    }

}
