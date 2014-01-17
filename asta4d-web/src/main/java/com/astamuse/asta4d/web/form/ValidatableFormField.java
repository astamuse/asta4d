package com.astamuse.asta4d.web.form;


public interface ValidatableFormField<T> {

    public void setFieldValue(T value);

    public T getFieldValue();

    public String getName();

    public boolean isTypeMismatched();

}
