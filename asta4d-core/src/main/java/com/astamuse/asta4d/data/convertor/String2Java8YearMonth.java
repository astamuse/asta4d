package com.astamuse.asta4d.data.convertor;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class String2Java8YearMonth extends AbstractString2Java8DateConvertor<YearMonth> implements DataValueConvertor<String, YearMonth> {

    //@formatter:off
    static final DateTimeFormatter[] dtfs = new DateTimeFormatter[] {
        DateTimeFormatter.ofPattern("yyyy-MM"),
        DateTimeFormatter.ofPattern("yyyyMM"),
    };
    //@formatter:on
    protected DateTimeFormatter[] availableFormatters() {
        return dtfs;
    }

    @Override
    protected YearMonth convert2Target(DateTimeFormatter formatter, String obj) {
        return YearMonth.parse(obj, formatter);
    }

}
