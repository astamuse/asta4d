package com.astamuse.asta4d.web.form.annotation.renderable.convert;

import java.lang.annotation.Annotation;

import com.astamuse.asta4d.util.annotation.AnnotationConvertor;
import com.astamuse.asta4d.web.form.annotation.FormField;
import com.astamuse.asta4d.web.form.annotation.renderable.SelectBox;
import com.astamuse.asta4d.web.form.field.FormFieldValueRenderer;

public class SelectBoxAnnotationConvertor implements AnnotationConvertor<SelectBox, FormField> {

    @Override
    public FormField convert(final SelectBox originalAnnotation) {
        return new FormField() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return FormField.class;
            }

            @Override
            public String name() {
                return originalAnnotation.name();
            }

            @Override
            public String editSelector() {
                return originalAnnotation.editSelector();
            }

            @Override
            public String displaySelector() {
                return originalAnnotation.displaySelector();
            }

            @Override
            public Class<? extends FormFieldValueRenderer> fieldValueRenderer() {
                return originalAnnotation.fieldValueRenderer();
            }
        };
    }

}
