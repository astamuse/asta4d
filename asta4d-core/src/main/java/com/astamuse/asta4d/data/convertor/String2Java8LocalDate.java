package com.astamuse.asta4d.data.convertor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class String2Java8LocalDate extends AbstractString2Java8DateConvertor<LocalDate> implements DataValueConvertor<String, LocalDate> {

    protected DateTimeFormatter[] availableFormatters() {
        return String2Java8Instant.dtfs;
    }

    @Override
    protected LocalDate convert2Target(DateTimeFormatter formatter, String obj) {
        return formatter.parse(obj, LocalDate::from);
    }

}
