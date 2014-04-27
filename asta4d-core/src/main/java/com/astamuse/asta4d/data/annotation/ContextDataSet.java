package com.astamuse.asta4d.data.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.astamuse.asta4d.data.ContextDataSetFactory;
import com.astamuse.asta4d.data.DefaultContextDataSetFactory;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ContextDataSet {

    public boolean singletonInContext() default false;

    public Class<? extends ContextDataSetFactory> factory() default DefaultContextDataSetFactory.class;
}
