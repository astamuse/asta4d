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

/**
 * convert string to enum
 * 
 * @author e-ryu
 * 
 */
@SuppressWarnings("rawtypes")
public class String2Enum implements DataValueConvertorTargetTypeConvertable<String, Enum> {
    @Override
    public DataValueConvertor<String, Enum> convert(final Class<Enum> targetType) {
        return new DataValueConvertor<String, Enum>() {
            @SuppressWarnings("unchecked")
            @Override
            public Enum convert(String s) throws UnsupportedValueException {
                if (StringUtils.isEmpty(s)) {
                    return null;
                }
                return Enum.valueOf(targetType, s);
            }
        };
    }

}
