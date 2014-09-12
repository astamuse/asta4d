package com.astamuse.asta4d.data;

import com.astamuse.asta4d.data.convertor.UnsupportedValueException;

public interface DataTypeTransformer {
    public Object transform(Class<?> srcType, Class<?> targetType, Object data) throws UnsupportedValueException;
}
