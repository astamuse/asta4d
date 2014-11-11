package com.astamuse.asta4d.data.convertor;

import org.joda.time.format.DateTimeFormatter;

public abstract class AbstractString2DateConvertor<S, T> {

    public T convert(S obj) throws UnsupportedValueException {
        for (DateTimeFormatter formatter : availableFormatters()) {
            try {
                return convert2Target(formatter, obj);
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
        throw new UnsupportedValueException();
    }

    protected abstract DateTimeFormatter[] availableFormatters();

    protected abstract T convert2Target(DateTimeFormatter formatter, S obj);
}
