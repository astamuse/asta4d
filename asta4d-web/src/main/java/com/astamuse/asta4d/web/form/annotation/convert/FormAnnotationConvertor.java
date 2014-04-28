package com.astamuse.asta4d.web.form.annotation.convert;

import java.lang.annotation.Annotation;

import com.astamuse.asta4d.data.ContextDataSetFactory;
import com.astamuse.asta4d.data.DefaultContextDataSetFactory;
import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.util.annotation.AnnotationConvertor;
import com.astamuse.asta4d.web.form.annotation.Form;

public class FormAnnotationConvertor implements AnnotationConvertor<Form, ContextDataSet> {

    @Override
    public ContextDataSet convert(Form originalAnnotation) {
        return new ContextDataSet() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return ContextDataSet.class;
            }

            @Override
            public boolean singletonInContext() {
                return true;
            }

            @Override
            public Class<? extends ContextDataSetFactory> factory() {
                return DefaultContextDataSetFactory.class;
            }
        };
    }

}
