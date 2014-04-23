package com.astamuse.asta4d.web.form.annotation.convert;

import com.astamuse.asta4d.util.annotation.AnnotationConvertor;
import com.astamuse.asta4d.web.annotation.QueryParam;
import com.astamuse.asta4d.web.form.annotation.FormField;

public class FormFieldAnnotationConvertor implements AnnotationConvertor<FormField, QueryParam> {

    @Override
    public QueryParam convert(FormField originalAnnotation) {
        return null;
    }

}
