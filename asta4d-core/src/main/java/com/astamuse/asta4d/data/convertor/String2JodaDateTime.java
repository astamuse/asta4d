package com.astamuse.asta4d.data.convertor;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class String2JodaDateTime extends AbstractString2DateConvertor<String, DateTime> implements DataValueConvertor<String, DateTime> {

    //@formatter:off
    static final DateTimeFormatter[] dtfs = new DateTimeFormatter[] {
        DateTimeFormat.forPattern("yyyyMMdd"),
        ISODateTimeFormat.dateOptionalTimeParser(),
        DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss.SSSZ"),
        DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss.SSS"),
        DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss"),
    };
    //@formatter:on

    protected DateTimeFormatter[] availableFormatters() {
        return dtfs;
    }

    @Override
    protected DateTime convert2Target(DateTimeFormatter formatter, String obj) {
        return formatter.parseDateTime(obj);
    }

}
