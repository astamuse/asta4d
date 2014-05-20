package com.astamuse.asta4d.web.form.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.astamuse.asta4d.util.annotation.ConvertableAnnotation;
import com.astamuse.asta4d.web.form.FormDeSerializer;
import com.astamuse.asta4d.web.form.SerializableFormDeserializer;
import com.astamuse.asta4d.web.form.annotation.convert.FormAnnotationConvertor;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@ConvertableAnnotation(FormAnnotationConvertor.class)
public @interface Form {
    public Class<? extends FormDeSerializer> deSerializer() default SerializableFormDeserializer.class;
}
