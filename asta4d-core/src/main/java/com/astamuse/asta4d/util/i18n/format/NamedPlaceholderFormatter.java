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

package com.astamuse.asta4d.util.i18n.format;

import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

public class NamedPlaceholderFormatter implements PlaceholderFormatter {

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
