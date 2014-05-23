package com.astamuse.asta4d.data.convertor;

public interface DataTypeConvertorTargetTypeConvertable<S, T> extends DataTypeConvertor<Class<T>, DataTypeConvertor<S, T>> {
    public DataTypeConvertor<S, T> convert(Class<T> targetType);
}
