/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.astamuse.asta4d.data.convertor;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class String2JodaLocalTime extends AbstractString2DateConvertor<LocalTime> implements DataValueConvertor<String, LocalTime> {

    //@formatter:off
    static final DateTimeFormatter[] dtfs = new DateTimeFormatter[] {
        ISODateTimeFormat.timeParser(),
        ISODateTimeFormat.timeNoMillis(),
        DateTimeFormat.forPattern("HHmmss.SSSZ"),
        DateTimeFormat.forPattern("HHmmss.SSS"),
        DateTimeFormat.forPattern("HHmmss"),
    };
    //@formatter:on
    protected DateTimeFormatter[] availableFormatters() {
        return dtfs;
    }

    @Override
    protected LocalTime convert2Target(DateTimeFormatter formatter, String obj) {
        return formatter.parseLocalTime(obj);
    }

}
