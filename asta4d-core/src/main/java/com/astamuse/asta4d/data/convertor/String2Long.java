/*
 * Copyright 2012 astamuse company,Ltd.
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

/**
 * Convert String to Long
 * 
 * @author e-ryu
 * 
 */
public class String2Long implements DataValueConvertor<String, Long> {

    @Override
    public Long convert(String s) throws UnsupportedValueException {
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException nfe) {
            throw new UnsupportedValueException();
        }
    }

}
