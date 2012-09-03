package org.jsoupit.template.render;

public interface ListConvertor<S, T> {
    public T convert(S obj);
}
