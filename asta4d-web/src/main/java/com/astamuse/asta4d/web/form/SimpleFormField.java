package com.astamuse.asta4d.web.form;

import com.astamuse.asta4d.data.ContextDataHolder;
import com.astamuse.asta4d.data.DataConvertorInvoker;
import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.web.WebApplicationConfiguration;

public abstract class SimpleFormField<T> extends ContextDataHolder implements FormField<T> {

    private final static DataConvertorInvoker DataConvertorInvoker = WebApplicationConfiguration.getWebApplicationConfiguration()
            .getDataConvertorInvoker();

    private T fieldValue;

    private Class<T> fieldValueType;

    @SuppressWarnings("unchecked")
    public SimpleFormField(Class<T> fieldValueType) {
        super(fieldValueType.isArray() ? String[].class : String.class);
        this.fieldValueType = fieldValueType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(String scope, String name, Object value) {
        super.setValue(scope, name, value);
        try {
            fieldValue = (T) DataConvertorInvoker.convert(value, fieldValueType);
        } catch (DataOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setFieldValue(T value) {
        fieldValue = value;
    }

    @Override
    public T getFieldValue() {
        return fieldValue;
    }

}
