package com.astamuse.asta4d.util.annotation;

import java.lang.annotation.Annotation;

public interface AnnotationConvertor<S extends Annotation, T extends Annotation> {
    public T convert(S originalAnnotation);
}
