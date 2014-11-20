package com.astamuse.asta4d.data.annotation;

import java.lang.annotation.Annotation;

import com.astamuse.asta4d.util.annotation.AnnotatedProperty;
import com.astamuse.asta4d.util.annotation.AnnotationConvertor;

public class ContextDataAnnotationConvertor implements AnnotationConvertor<ContextData, AnnotatedProperty> {
    @Override
    public AnnotatedProperty convert(final ContextData originalAnnotation) {
        return new AnnotatedProperty() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return AnnotatedProperty.class;
            }

            @Override
            public String name() {
                return originalAnnotation.name();
            }
        };
    }

}
