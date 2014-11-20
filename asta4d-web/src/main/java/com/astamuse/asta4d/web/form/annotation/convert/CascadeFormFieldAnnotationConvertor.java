package com.astamuse.asta4d.web.form.annotation.convert;

import java.lang.annotation.Annotation;

import com.astamuse.asta4d.util.annotation.AnnotationConvertor;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.annotation.FormField;
import com.astamuse.asta4d.web.form.field.FormFieldValueRenderer;
import com.astamuse.asta4d.web.form.field.impl.DoNothingFormFieldRenderer;

public class CascadeFormFieldAnnotationConvertor implements AnnotationConvertor<CascadeFormField, FormField> {

    @Override
    public FormField convert(final CascadeFormField originalAnnotation) {
        return new FormField() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return FormField.class;
            }

            @Override
            public String name() {
                return originalAnnotation.name();
            }

            public String nameLabel() {
                return originalAnnotation.nameLabel();
            }

            public String message() {
                return originalAnnotation.message();
            }

            @Override
            public String editSelector() {
                return originalAnnotation.containerSelector();
            }

            @Override
            public String displaySelector() {
                return originalAnnotation.containerSelector();
            }

            @Override
            public Class<? extends FormFieldValueRenderer> fieldValueRenderer() {
                return DoNothingFormFieldRenderer.class;
            }

        };
    }

}
