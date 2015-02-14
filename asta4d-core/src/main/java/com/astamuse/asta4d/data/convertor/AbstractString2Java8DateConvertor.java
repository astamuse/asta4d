package com.astamuse.asta4d.data.convertor;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractString2Java8DateConvertor<T> {

    public T convert(String s) throws UnsupportedValueException {
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        for (DateTimeFormatter formatter : availableFormatters()) {
            try {
                T tt = convert2Target(formatter, s);
                return tt;
            } catch (DateTimeParseException e) {
                System.out.println(formatter.toString());
                e.printStackTrace();
                continue;
            }
        }
        throw new UnsupportedValueException();
    }

    protected abstract DateTimeFormatter[] availableFormatters();

    protected abstract T convert2Target(DateTimeFormatter formatter, String obj);
}
