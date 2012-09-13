package com.astamuse.asta4d.data;

public interface DataConvertor<S, T> {
    public T convert(S obj);
}
