package com.astamuse.asta4d.web.form.annotation.renderable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.astamuse.asta4d.util.annotation.ConvertableAnnotation;
import com.astamuse.asta4d.web.form.annotation.renderable.convert.AvailabeWhenEditOnlyAnnotationConvertor;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ConvertableAnnotation(AvailabeWhenEditOnlyAnnotationConvertor.class)
public @interface AvailableWhenEditOnly {
    String selector();
}
