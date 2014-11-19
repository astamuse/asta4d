package com.astamuse.asta4d.data.convertor;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormatter;

public abstract class AbstractString2DateConvertor<T> implements DataValueConvertor<String, T> {

    public T convert(String s) throws UnsupportedValueException {
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        for (DateTimeFormatter formatter : availableFormatters()) {
            try {
                return convert2Target(formatter, s);
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
        throw new UnsupportedValueException();
    }

    protected abstract DateTimeFormatter[] availableFormatters();

    protected abstract T convert2Target(DateTimeFormatter formatter, String obj);
}
