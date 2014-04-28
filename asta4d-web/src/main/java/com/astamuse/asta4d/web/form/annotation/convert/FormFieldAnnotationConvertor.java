package com.astamuse.asta4d.web.form.annotation.convert;

import java.lang.annotation.Annotation;

import com.astamuse.asta4d.data.TypeUnMacthPolicy;
import com.astamuse.asta4d.util.annotation.AnnotationConvertor;
import com.astamuse.asta4d.web.annotation.QueryParam;
import com.astamuse.asta4d.web.form.annotation.FormField;

public class FormFieldAnnotationConvertor implements AnnotationConvertor<FormField, QueryParam> {

    @Override
    public QueryParam convert(final FormField originalAnnotation) {
        return new QueryParam() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return QueryParam.class;
            }

            @Override
            public TypeUnMacthPolicy typeUnMatch() {
                return TypeUnMacthPolicy.DEFAULT_VALUE_AND_TRACE;
            }

            @Override
            public String name() {
                return originalAnnotation.name();
            }
        };
    }

}
