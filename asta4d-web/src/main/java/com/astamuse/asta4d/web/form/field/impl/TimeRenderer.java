package com.astamuse.asta4d.web.form.field.impl;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.joda.time.LocalTime;
import org.joda.time.base.BaseDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeRenderer extends InputDefaultRenderer {

    protected static final DateTimeFormatter jodaFormatter = DateTimeFormat.forPattern("HH:mm:ss.SSS");

    protected static final java.time.format.DateTimeFormatter java8Formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    protected String getNonNullString(Object value) {
        if (value instanceof Date) {
            return jodaFormatter.print(((Date) value).getTime());
        } else if (value instanceof BaseDateTime) {
            return jodaFormatter.print(((BaseDateTime) value).getMillis());
        } else if (value instanceof LocalTime) {
            return ((LocalTime) value).toString(jodaFormatter);
        } else if (value instanceof java.time.LocalTime) {
            return java8Formatter.format((java.time.LocalTime) value);
        } else if (value instanceof java.time.LocalDateTime) {
            return java8Formatter.format((java.time.LocalDateTime) value);
        } else if (value instanceof Instant) {
            Instant ins = (Instant) value;
            java.time.LocalDateTime ld = ZonedDateTime.ofInstant(ins, ZoneId.systemDefault()).toLocalDateTime();
            return java8Formatter.format(ld);
        } else {
            return super.getNonNullString(value);
        }

    }
}
