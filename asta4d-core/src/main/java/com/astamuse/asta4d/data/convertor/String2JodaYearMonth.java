package com.astamuse.asta4d.data.convertor;

import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class String2JodaYearMonth extends AbstractString2DateConvertor<YearMonth> {

    //@formatter:off
    static final DateTimeFormatter[] dtfs = new DateTimeFormatter[] {
        DateTimeFormat.forPattern("yyyy-MM"),
        DateTimeFormat.forPattern("yyyyMM"),
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
