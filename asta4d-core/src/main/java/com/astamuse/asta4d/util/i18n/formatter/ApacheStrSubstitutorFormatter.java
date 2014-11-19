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

package com.astamuse.asta4d.util.i18n.formatter;

import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

/**
 * A message formatter using Apache common lang3's StrSubstitutor. The variable
 * name should be wrapped with a pair of braces.
 * <p>
 * <i> The quick brown {fox} jumps over the lazy {dog} </i>
 * <p>
 * In above statement, the fox and dog will be treated as variable names. The
 * default escape character is '\'.
 * 
 * @author e-ryu
 *
 */
public class ApacheStrSubstitutorFormatter implements MappedValueFormatter {

    private String prefix = "{";

    private String suffix = "}";

    private char escape = '\\';

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setEscape(char escape) {
        this.escape = escape;
    }

    @Override
    public String format(String pattern, Map<String, Object> paramMap) {
        StrSubstitutor sub = new StrSubstitutor(paramMap, prefix, suffix, escape);
        return sub.replace(pattern);
    }
}
