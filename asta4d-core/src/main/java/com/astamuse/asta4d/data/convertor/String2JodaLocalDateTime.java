package com.astamuse.asta4d.data.convertor;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;

public class String2JodaLocalDateTime extends AbstractString2DateConvertor<String, LocalDateTime> implements
        DataValueConvertor<String, LocalDateTime> {

    protected DateTimeFormatter[] availableFormatters() {
        return String2JodaDateTime.dtfs;
    }

    @Override
    protected LocalDateTime convert2Target(DateTimeFormatter formatter, String obj) {
        return formatter.parseLocalDateTime(obj);
    }

}
