/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.astamuse.asta4d.util.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;

public class AnnotatedPropertyInfo {

    private String name;

    private String beanPropertyName;

    private Field field;

    private Method getter;

    private Method setter;

    private Class type;

    private List<Annotation> annotationList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeanPropertyName() {
        return beanPropertyName;
    }

    public void setBeanPropertyName(String beanPropertyName) {
        this.beanPropertyName = beanPropertyName;
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

    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getAnnotation(Class<A> annotationCls) {
        for (Annotation anno : annotationList) {
            if (annotationCls.isAssignableFrom(anno.getClass())) {
                return (A) anno;
            }
        }
        return null;
    }

    public void setAnnotations(List<Annotation> annotationList) {
        this.annotationList = annotationList;
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
        return (name == null) ? 0 : name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AnnotatedPropertyInfo other = (AnnotatedPropertyInfo) obj;
        if (annotationList == null) {
            if (other.annotationList != null)
                return false;
        } else if (!annotationList.equals(other.annotationList))
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
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AnnotatedPropertyInfo [name=" + name + "]";
    }

}
