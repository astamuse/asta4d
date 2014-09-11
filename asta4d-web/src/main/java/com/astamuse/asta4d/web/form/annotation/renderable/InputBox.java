package com.astamuse.asta4d.web.form.annotation.renderable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.astamuse.asta4d.util.annotation.ConvertableAnnotation;
import com.astamuse.asta4d.web.form.annotation.renderable.convert.InputBoxAnnotationConvertor;
import com.astamuse.asta4d.web.form.field.FormFieldValueRenderer;
import com.astamuse.asta4d.web.form.field.impl.InputBoxRenderer;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
@ConvertableAnnotation(InputBoxAnnotationConvertor.class)
public @interface InputBox {
    String name() default "";

    String editSelector() default "";

    String displaySelector() default "";

    Class<? extends FormFieldValueRenderer> fieldValueRenderer() default InputBoxRenderer.class;
}
