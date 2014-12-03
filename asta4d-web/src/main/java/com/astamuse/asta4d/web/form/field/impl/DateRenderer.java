package com.astamuse.asta4d.web.form.field.impl;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.joda.time.LocalDate;
import org.joda.time.base.BaseDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateRenderer extends InputDefaultRenderer {

    protected static final DateTimeFormatter jodaFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    protected static final java.time.format.DateTimeFormatter java8Formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

    protected String getNonNullString(Object value) {
        /*
         * For the java 8 LocalDate, default #toString() is good enough,
         *  and for java 8 Instant, converting to LocalDate is not bad.
         * 
         */
        if (value instanceof Date) {
            return jodaFormatter.print(((Date) value).getTime());
        } else if (value instanceof BaseDateTime) {
            return jodaFormatter.print(((BaseDateTime) value).getMillis());
        } else if (value instanceof LocalDate) {
            return ((LocalDate) value).toString(jodaFormatter);
        } else if (value instanceof java.time.LocalDate) {
            return java8Formatter.format((java.time.LocalDate) value);
        } else if (value instanceof java.time.LocalDateTime) {
            return java8Formatter.format((java.time.LocalDateTime) value);
        } else if (value instanceof Instant) {
            Instant ins = (Instant) value;
            java.time.LocalDate ld = ZonedDateTime.ofInstant(ins, ZoneId.systemDefault()).toLocalDate();
            return java8Formatter.format(ld);
        } else {
            return super.getNonNullString(value);
        }

    }
}
