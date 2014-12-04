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
