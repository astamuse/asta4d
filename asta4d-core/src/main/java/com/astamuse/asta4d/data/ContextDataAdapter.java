package com.astamuse.asta4d.data;

public interface ContextDataAdapter {

    public Class<?> getSourceType();

    public Class<?> getTargetType();

    public Object convert(Object obj);

}
