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

import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class String2JodaYearMonth extends AbstractString2DateConvertor<YearMonth> implements DataValueConvertor<String, YearMonth> {

    //@formatter:off
    static final DateTimeFormatter[] dtfs = new DateTimeFormatter[] {
        DateTimeFormat.forPattern("yyyy-MM"),
        DateTimeFormat.forPattern("yyyyMM"),
    };
    //@formatter:on
    protected DateTimeFormatter[] availableFormatters() {
        return dtfs;
    }

    @Override
    protected YearMonth convert2Target(DateTimeFormatter formatter, String obj) {
        return YearMonth.parse(obj, formatter);
    }

}
