package com.astamuse.asta4d.data.convertor;

/**
 * 
 * Can be used to implement a convertor against parent type, such as enum.
 * 
 * @see String2Enum
 * 
 * @author e-ryu
 * 
 * @param <S>
 * @param <T>
 */
public interface DataValueConvertorTargetTypeConvertable<S, T> extends DataValueConvertor<Class<T>, DataValueConvertor<S, T>> {
    public DataValueConvertor<S, T> convert(Class<T> targetType);
}
