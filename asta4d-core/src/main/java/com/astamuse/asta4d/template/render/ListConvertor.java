package com.astamuse.asta4d.template.render;

public interface ListConvertor<S, T> {
    public T convert(S obj);
}
