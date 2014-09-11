package com.astamuse.asta4d.web.form.annotation.renderable.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.astamuse.asta4d.util.annotation.AnnotationConvertor;
import com.astamuse.asta4d.web.form.annotation.FormField;
import com.astamuse.asta4d.web.form.field.FormFieldValueRenderer;

public class CommonInputAnnotationConvertor implements AnnotationConvertor<Annotation, FormField> {

    @Override
    public FormField convert(final Annotation originalAnnotation) {
        return new FormField() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return FormField.class;
            }

            @Override
            public String name() {
                return getValue(originalAnnotation, "name");
            }

            @Override
            public String editSelector() {
                return getValue(originalAnnotation, "editSelector");
            }

            @Override
            public String displaySelector() {
                return getValue(originalAnnotation, "displaySelector");
            }

            @Override
            public Class<? extends FormFieldValueRenderer> fieldValueRenderer() {
                return getValue(originalAnnotation, "fieldValueRenderer");
            }
        };

    }

    @SuppressWarnings("unchecked")
    private <T> T getValue(Annotation originalAnnotation, String name) {
        try {
            Method m = originalAnnotation.annotationType().getMethod(name);
            return (T) m.invoke(originalAnnotation);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
