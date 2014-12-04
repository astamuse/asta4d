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

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormatter;

public abstract class AbstractString2DateConvertor<T> {

    public T convert(String s) throws UnsupportedValueException {
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        for (DateTimeFormatter formatter : availableFormatters()) {
            try {
                return convert2Target(formatter, s);
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
        throw new UnsupportedValueException();
    }

    protected abstract DateTimeFormatter[] availableFormatters();

    protected abstract T convert2Target(DateTimeFormatter formatter, String obj);
}
