package com.astamuse.asta4d.data.convertor;

import static java.time.temporal.ChronoField.INSTANT_SECONDS;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;

public class String2Java8Instant extends AbstractString2Java8DateConvertor<Instant> implements DataValueConvertor<String, Instant> {

    //@formatter:off
    private static final DateTimeFormatter ISO_DATE_TIME_ZONE_VARIANT = new DateTimeFormatterBuilder()
                                                    .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                                    .optionalStart()
                                                    .appendOffset("+HHmm", "Z")
                                                    .optionalStart()
                                                    .appendLiteral('[')
                                                    .parseCaseSensitive()
                                                    .appendZoneRegionId()
                                                    .appendLiteral(']')
                                                    .toFormatter();
    //@formatter:on

    //@formatter:off
    static final DateTimeFormatter[] dtfs = new DateTimeFormatter[] {
        DateTimeFormatter.ofPattern("yyyyMMdd"),
        DateTimeFormatter.ISO_INSTANT,
        DateTimeFormatter.ISO_DATE_TIME,
        ISO_DATE_TIME_ZONE_VARIANT,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ISO_DATE,
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSZ"),
        DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSS"),
        DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"),
    };
    //@formatter:on

    protected DateTimeFormatter[] availableFormatters() {
        return dtfs;
    }

    @Override
    protected Instant convert2Target(DateTimeFormatter formatter, String obj) {
        TemporalAccessor temporal = formatter.parse(obj);
        if (temporal instanceof Instant) {
            return (Instant) temporal;
        }
        Objects.requireNonNull(temporal, "temporal");
        try {
            long instantSecs = temporal.getLong(INSTANT_SECONDS);
            int nanoOfSecond = temporal.get(NANO_OF_SECOND);
            return Instant.ofEpochSecond(instantSecs, nanoOfSecond);
        } catch (DateTimeException e) {
            // which means it may be a local date time or local date only string
            ZonedDateTime zdt;
            try {
                LocalDateTime ldt = LocalDateTime.from(temporal);
                zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
            } catch (DateTimeException ex) {
                // a local date only string
                zdt = ZonedDateTime.of(LocalDate.from(temporal).atStartOfDay(), ZoneId.systemDefault());
            }

            return zdt.toInstant();
        }

    }
}
