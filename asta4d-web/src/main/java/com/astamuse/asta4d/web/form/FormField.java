package com.astamuse.asta4d.web.form;

import com.astamuse.asta4d.render.Renderer;

public interface FormField<T> {

    public Renderer fieldValueRenderer();

    public void setFieldValue(T value);

    public T getFieldValue();

    public String getName();

}
