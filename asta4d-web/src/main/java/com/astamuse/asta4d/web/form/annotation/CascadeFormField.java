package com.astamuse.asta4d.web.form.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.astamuse.asta4d.util.annotation.ConvertableAnnotation;
import com.astamuse.asta4d.web.form.annotation.convert.CascadeFormFieldAnnotationConvertor;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
@ConvertableAnnotation(CascadeFormFieldAnnotationConvertor.class)
public @interface CascadeFormField {

    String name() default "";

    String nameLabel() default "";

    String message() default "";

    String arrayLengthField() default "";

    String containerSelector() default "";

}
