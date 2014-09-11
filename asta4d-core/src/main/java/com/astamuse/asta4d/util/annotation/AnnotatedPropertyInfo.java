package com.astamuse.asta4d.util.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AnnotatedPropertyInfo<A extends Annotation> {

    private String name;

    private Field field;

    private Method getter;

    private Method setter;

    private A annotation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Method getGetter() {
        return getter;
    }

    public void setGetter(Method getter) {
        this.getter = getter;
    }

    public Method getSetter() {
        return setter;
    }

    public void setSetter(Method setter) {
        this.setter = setter;
    }

    public A getAnnotation() {
        return annotation;
    }

    public void setAnnotation(A annotation) {
        this.annotation = annotation;
    }

}
