package com.astamuse.asta4d.data.convertor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class String2Java8LocalTime extends AbstractString2Java8DateConvertor<LocalTime> implements DataValueConvertor<String, LocalTime> {

    //@formatter:off
    public static final DateTimeFormatter ISO_TIME_TIME_ZONE_VARIANT = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_TIME)
                .optionalStart()
                .appendOffset("+HHmm", "Z")
                .toFormatter();
    
    static final DateTimeFormatter[] dtfs = new DateTimeFormatter[] {
        DateTimeFormatter.ISO_LOCAL_TIME,
        DateTimeFormatter.ISO_TIME,
        ISO_TIME_TIME_ZONE_VARIANT,
        DateTimeFormatter.ofPattern("HHmmss.SSSZ"),
        DateTimeFormatter.ofPattern("HHmmss.SSS"),
        DateTimeFormatter.ofPattern("HHmmss"),
    };
    //@formatter:on
    protected DateTimeFormatter[] availableFormatters() {
        return dtfs;
    }

    @Override
    protected LocalTime convert2Target(DateTimeFormatter formatter, String obj) {
        return formatter.parse(obj, LocalTime::from);
    }

}
