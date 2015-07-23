package com.astamuse.asta4d.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class Java8TimeUtil {

    private static final ZoneOffset _defaultZoneOffset;
    static {
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
        _defaultZoneOffset = zdt.getOffset();
    }

    public static final ZoneOffset defaultZoneOffset() {
        return _defaultZoneOffset;
    }
}
