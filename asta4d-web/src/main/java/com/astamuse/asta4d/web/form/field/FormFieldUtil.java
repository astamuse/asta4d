package com.astamuse.asta4d.web.form.field;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;
import com.astamuse.asta4d.web.form.annotation.FormField;

public class FormFieldUtil {

    private static class UnModifiableAnnotatedPropertyInfo<A extends Annotation> extends AnnotatedPropertyInfo<A> {

        private AnnotatedPropertyInfo<A> prop;

        public UnModifiableAnnotatedPropertyInfo(AnnotatedPropertyInfo<A> prop) {
            this.prop = prop;
        }

        public String getName() {
            return prop.getName();
        }

        public void setName(String name) {
            throw new UnsupportedOperationException();
        }

        public Field getField() {
            return prop.getField();
        }

        public void setField(Field field) {
            throw new UnsupportedOperationException();
        }

        public Method getGetter() {
            return prop.getGetter();
        }

        public void setGetter(Method getter) {
            throw new UnsupportedOperationException();
        }

        public Method getSetter() {
            return prop.getSetter();
        }

        public void setSetter(Method setter) {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("rawtypes")
        public Class getType() {
            return prop.getType();
        }

        @SuppressWarnings("rawtypes")
        public void setType(Class type) {
            throw new UnsupportedOperationException();
        }

        public A getAnnotation() {
            return prop.getAnnotation();
        }

        public void setAnnotation(A annotation) {
            throw new UnsupportedOperationException();
        }

        public void assginValue(Object instance, Object value) throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException {
            prop.assginValue(instance, value);
        }

        public Object retrieveValue(Object instance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            return prop.retrieveValue(instance);
        }

        public int hashCode() {
            return prop.hashCode();
        }

        public boolean equals(Object obj) {
            return prop.equals(obj);
        }

        public String toString() {
            return prop.toString();
        }

    }

    private static final Map<String, List<AnnotatedPropertyInfo<FormField>>> FieldsMap = new ConcurrentHashMap<>();

    public static final List<AnnotatedPropertyInfo<FormField>> retrieveFormFields(Class formCls) throws DataOperationException {
        List<AnnotatedPropertyInfo<FormField>> list = FieldsMap.get(formCls.getName());
        if (list == null) {
            try {
                List<AnnotatedPropertyInfo<FormField>> retrieveList = AnnotatedPropertyUtil.retrieveProperties(formCls, FormField.class);
                list = new ArrayList<>(retrieveList.size());
                for (AnnotatedPropertyInfo<FormField> prop : retrieveList) {

                    if (prop.getField() == null) {
                        if (prop.getGetter() == null || prop.getSetter() == null) {
                            throw new DataOperationException("@FormField annotated methods must be paired as getter and setter");
                        }
                    }

                    AnnotatedPropertyInfo<FormField> newProp = new AnnotatedPropertyInfo<FormField>();
                    BeanUtils.copyProperties(newProp, prop);
                    String declaredName = prop.getAnnotation().name();
                    if (StringUtils.isNotEmpty(declaredName)) {
                        newProp.setName(declaredName);
                    }
                    list.add(new UnModifiableAnnotatedPropertyInfo<FormField>(newProp));
                }
                FieldsMap.put(formCls.getName(), list);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    }

}
