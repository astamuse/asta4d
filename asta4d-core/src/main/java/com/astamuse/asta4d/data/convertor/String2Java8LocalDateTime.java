package com.astamuse.asta4d.data.convertor;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class String2Java8LocalDateTime extends AbstractString2Java8DateConvertor<LocalDateTime> implements
        DataValueConvertor<String, LocalDateTime> {

    protected DateTimeFormatter[] availableFormatters() {
        return String2Java8Instant.dtfs;
    }

    @Override
    protected LocalDateTime convert2Target(DateTimeFormatter formatter, String obj) {
        TemporalAccessor temporal = formatter.parse(obj);
        try {
            return LocalDateTime.from(temporal);
        } catch (DateTimeException ex) {
            // a local date only string
            return LocalDate.from(temporal).atStartOfDay();
        }
    }

}
