package com.astamuse.asta4d.data;

public interface DataTypeTransformer {
    public Object transform(Class<?> srcType, Class<?> targetType, Object data) throws DataOperationException;
}
