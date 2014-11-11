package com.astamuse.asta4d.data.convertor;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

public class String2JodaLocalDate extends AbstractString2DateConvertor<String, LocalDate> implements DataValueConvertor<String, LocalDate> {

    protected DateTimeFormatter[] availableFormatters() {
        return String2JodaDateTime.dtfs;
    }

    @Override
    protected LocalDate convert2Target(DateTimeFormatter formatter, String obj) {
        return formatter.parseLocalDate(obj);
    }

}
