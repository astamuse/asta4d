package com.astamuse.asta4d.data.convertor;

import java.util.Date;

import org.joda.time.format.DateTimeFormatter;

public class String2Date extends AbstractString2DateConvertor<Date> {

    protected DateTimeFormatter[] availableFormatters() {
        return String2JodaDateTime.dtfs;
    }

    @Override
    protected Date convert2Target(DateTimeFormatter formatter, String obj) {
        return formatter.parseDateTime(obj).toDate();
    }

}
