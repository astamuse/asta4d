package com.astamuse.asta4d.data;

public class DefaultContextDataSetFactory implements ContextDataSetFactory {

    @SuppressWarnings("rawtypes")
    @Override
    public Object createInstance(Class cls) {
        try {
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
