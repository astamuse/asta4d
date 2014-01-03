package com.astamuse.asta4d.data;

public interface DataConvertorInvoker {
    public Object convert(Object data, Class<?> targetType) throws DataOperationException;
}
