package com.astamuse.asta4d.web.dispatch.mapping;

public class ResultDescriptor {

    private Class<?> resultTypeIdentifier = null;

    private Object resultInstanceIdentifier = null;

    private Object contentProvider = null;

    @SuppressWarnings("rawtypes")
    public ResultDescriptor(Object resultIdentifier, Object contentProvider) {
        super();
        this.contentProvider = contentProvider;
        if (resultIdentifier instanceof Class) {
            resultTypeIdentifier = (Class) resultIdentifier;
        } else {
            resultInstanceIdentifier = resultIdentifier;
        }
    }

    public Class<?> getResultTypeIdentifier() {
        return resultTypeIdentifier;
    }

    public Object getResultInstanceIdentifier() {
        return resultInstanceIdentifier;
    }

    public Object getContentProvider() {
        return contentProvider;
    }

}
