package com.astamuse.asta4d.web.form.backup;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javassist.bytecode.FieldInfo;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.astamuse.asta4d.data.annotation.ContextDataSet;

@ContextDataSet
public abstract class AbstractValidatableForm {

    private final static Map<String, List<Field>> ValidationFieldListMap = new ConcurrentHashMap<>();

    public AbstractValidatableForm() {
        super();
    }

    public boolean isValid() {
        List<Field> validationTargets = ValidationFieldListMap.get(this.getClass().getName());
        if (validationTargets == null) {
            validationTargets = retrieveValidationFieldList();
            ValidationFieldListMap.put(this.getClass().getName(), validationTargets);
        }

        return validateTypeConversion(validationTargets) && validateValues(validationTargets);
    }

    protected abstract boolean validateValues(List<Field> fieldList);

    protected boolean validateTypeConversion(List<Field> fieldList) {
        try {
            ValidatableFormField v;
            boolean hasTypeMismachedField = false;
            for (Field field : fieldList) {
                if (ValidatableFormField.class.isAssignableFrom(field.getType())) {
                    v = (ValidatableFormField) FieldUtils.readField(field, this, true);
                    if (v.isTypeMismatched()) {
                        hasTypeMismachedField = true;
                        addMessage(v.getName(), retrieveTypeMismatchedMessage(field));
                    }
                }
            }
            return !hasTypeMismachedField;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected String retrieveTypeMismatchedMessage(Field field) {
        TypeMismatch tmm = field.getAnnotation(TypeMismatch.class);
        if (tmm == null) {
            return "type mismached";
        } else {
            return tmm.message();
        }
    }

    protected List<Field> retrieveValidationFieldList() {

        List<Field> fieldList = new LinkedList<>();

        // retrieve fields information
        String objCls = Object.class.getName();
        Field[] flds;
        FieldInfo fi;
        Class cls = this.getClass();
        while (!cls.getName().equals(objCls)) {
            flds = cls.getDeclaredFields();
            for (Field field : flds) {
                fieldList.add(field);
            }// end for loop
            cls = cls.getSuperclass();
        }
        return fieldList;
    }

    public void addMessage(String name, String message) {
        System.err.println("name=" + name + ",[" + message + "]");
    }

}
