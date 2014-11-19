package com.astamuse.asta4d.data.convertor;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class String2JodaLocalTime extends AbstractString2DateConvertor<LocalTime> {

    //@formatter:off
    static final DateTimeFormatter[] dtfs = new DateTimeFormatter[] {
        ISODateTimeFormat.timeParser(),
        ISODateTimeFormat.timeNoMillis(),
        DateTimeFormat.forPattern("HHmmss.SSSZ"),
        DateTimeFormat.forPattern("HHmmss.SSS"),
        DateTimeFormat.forPattern("HHmmss"),
    };
    //@formatter:on
    protected DateTimeFormatter[] availableFormatters() {
        return dtfs;
    }

    @Override
    protected LocalTime convert2Target(DateTimeFormatter formatter, String obj) {
        return formatter.parseLocalTime(obj);
    }

}
