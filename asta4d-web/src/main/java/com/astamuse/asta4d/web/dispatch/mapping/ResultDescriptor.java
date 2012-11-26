package com.astamuse.asta4d.web.dispatch.mapping;

import com.astamuse.asta4d.web.dispatch.response.ContentWriter;

public class ResultDescriptor {

    private Class<?> resultTypeIdentifier = null;

    private Object resultInstanceIdentifier = null;

    private Object contentProvider = null;

    private ContentWriter writer = null;

    @SuppressWarnings("rawtypes")
    public ResultDescriptor(Object resultIdentifier, Object contentProvider, ContentWriter writer) {
        super();
        this.contentProvider = contentProvider;
        this.writer = writer;
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

    public ContentWriter getWriter() {
        return writer;
    }

}
