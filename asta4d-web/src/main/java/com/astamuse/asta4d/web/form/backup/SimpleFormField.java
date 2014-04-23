package com.astamuse.asta4d.web.form.backup;

import com.astamuse.asta4d.data.ContextDataHolder;

public class SimpleFormField<T> extends ContextDataHolder implements ValidatableFormField<T> {

    private T fieldValue;

    private Class<T> fieldValueType;

    private boolean isTypeMismatched = false;

    @SuppressWarnings("unchecked")
    public SimpleFormField(Class<T> fieldValueType) {
        super(fieldValueType.isArray() ? String[].class : String.class);
        this.fieldValueType = fieldValueType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setData(String scope, String name, Object value) {
        super.setData(scope, name, value);
        /*
        try {
            fieldValue = (T) DataConvertorInvoker.convert(value, fieldValueType);
        } catch (Exception e) {
            isTypeMismatched = true;
        }
        */
    }

    @Override
    public void setFieldValue(T value) {
        fieldValue = value;
    }

    @Override
    public T getFieldValue() {
        return fieldValue;
    }

    @Override
    public boolean isTypeMismatched() {
        return isTypeMismatched;
    }

}
