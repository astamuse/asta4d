package com.astamuse.asta4d.web.dispatch.request.transformer;

import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;

public class SimpleTypeMatchTransformer implements ResultTransformer {

    private Class<?> resultTypeIdentifier = null;

    private Object resultInstanceIdentifier = null;

    private Object transformedResult;

    public SimpleTypeMatchTransformer(Object obj, Object transformedResult) {
        super();
        this.transformedResult = transformedResult;
        if (obj instanceof Class) {
            resultTypeIdentifier = (Class<?>) obj;
        } else {
            resultInstanceIdentifier = obj;
        }
    }

    public boolean isAsDefaultMatch() {
        return resultTypeIdentifier == null && resultInstanceIdentifier == null;
    }

    @Override
    public Object transformToContentProvider(Object result) {
        if (resultTypeIdentifier == null && resultInstanceIdentifier == null) {
            return this.transformedResult;
        } else if (result == null) {
            return null;
        } else if (resultTypeIdentifier != null) {
            if (resultTypeIdentifier.isAssignableFrom(result.getClass())) {
                return this.transformedResult;
            } else if (resultTypeIdentifier.equals(result.getClass())) {
                return this.transformedResult;
            }
        } else if (resultInstanceIdentifier != null) {
            if (resultInstanceIdentifier == result || resultInstanceIdentifier.equals(result)) {
                return this.transformedResult;
            }
        }
        return null;
    }

}
