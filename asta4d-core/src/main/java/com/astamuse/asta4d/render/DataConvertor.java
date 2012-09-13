package com.astamuse.asta4d.render;

public interface DataConvertor<S, T> {
    public T convert(S obj);
}
