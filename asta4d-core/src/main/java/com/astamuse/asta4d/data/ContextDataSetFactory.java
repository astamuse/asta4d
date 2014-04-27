package com.astamuse.asta4d.data;

public interface ContextDataSetFactory {
    @SuppressWarnings("rawtypes")
    public Object createInstance(Class cls);
}
