package com.astamuse.asta4d.util.collection;

import java.util.function.Function;

public interface RowConvertorBuilder {

    public static <S, T> RowConvertor<S, T> map(Function<S, T> mapper) {
        return new RowConvertor<S, T>() {
            @Override
            public T convert(int rowIndex, S obj) {
                return mapper.apply(obj);
            }
        };
    }

    public static <S, T> RowConvertor<S, T> parallel(RowConvertor<S, T> convertor) {
        return new RowConvertor<S, T>() {
            @Override
            public T convert(int rowIndex, S obj) {
                return convertor.convert(rowIndex, obj);
            }

            public boolean isParallel() {
                return true;
            }
        };
    }

    public static <S, T> RowConvertor<S, T> parallel(Function<S, T> mapper) {
        return new RowConvertor<S, T>() {
            @Override
            public T convert(int rowIndex, S obj) {
                return mapper.apply(obj);
            }

            public boolean isParallel() {
                return true;
            }
        };
    }
}
