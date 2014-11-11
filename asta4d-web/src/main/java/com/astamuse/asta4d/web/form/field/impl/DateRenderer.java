package com.astamuse.asta4d.web.form.field.impl;

import java.util.Date;

import org.joda.time.LocalDate;
import org.joda.time.base.BaseDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateRenderer extends InputDefaultRenderer {

    protected static final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");

    protected String getNonNullString(Object value) {
        if (value instanceof Date) {
            return dtf.print(((Date) value).getTime());
        } else if (value instanceof BaseDateTime) {
            return dtf.print(((BaseDateTime) value).getMillis());
        } else if (value instanceof LocalDate) {
            return ((LocalDate) value).toString(dtf);
        } else {
            return super.getNonNullString(value);
        }

    }
}
