package com.astamuse.asta4d.util.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.reflect.FieldUtils;

public class AnnotatedPropertyInfo<A extends Annotation> {

    private String name;

    private Field field;

    private Method getter;

    private Method setter;

    private Class type;

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

    @SuppressWarnings("rawtypes")
    public Class getType() {
        return type;
    }

    @SuppressWarnings("rawtypes")
    public void setType(Class type) {
        this.type = type;
    }

    public A getAnnotation() {
        return annotation;
    }

    public void setAnnotation(A annotation) {
        this.annotation = annotation;
    }

    public void assginValue(Object instance, Object value) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        if (this.field == null) {
            setter.invoke(instance, value);
        } else {
            FieldUtils.writeField(field, instance, value, true);
        }
    }

    public Object retrieveValue(Object instance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (this.field == null) {
            return getter.invoke(instance);
        } else {
            return FieldUtils.readField(field, instance, true);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((annotation == null) ? 0 : annotation.hashCode());
        result = prime * result + ((field == null) ? 0 : field.hashCode());
        result = prime * result + ((getter == null) ? 0 : getter.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((setter == null) ? 0 : setter.hashCode());
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AnnotatedPropertyInfo other = (AnnotatedPropertyInfo) obj;
        if (annotation == null) {
            if (other.annotation != null)
                return false;
        } else if (!annotation.equals(other.annotation))
            return false;
        if (field == null) {
            if (other.field != null)
                return false;
        } else if (!field.equals(other.field))
            return false;
        if (getter == null) {
            if (other.getter != null)
                return false;
        } else if (!getter.equals(other.getter))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (setter == null) {
            if (other.setter != null)
                return false;
        } else if (!setter.equals(other.setter))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AnnotatedPropertyInfo [name=" + name + "]";
    }

}
