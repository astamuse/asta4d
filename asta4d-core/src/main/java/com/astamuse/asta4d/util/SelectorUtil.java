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

package com.astamuse.asta4d.util;

public class SelectorUtil {

    public final static String not(String not) {
        return not(null, not);
    }

    public final static String not(String prefix, String not) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix == null ? "*" : prefix);
        sb.append(":not(").append(not).append(")");
        return sb.toString();
    }

    public final static String attr(String attr) {
        return attr(attr, null);
    }

    public final static String attr(String attr, String value) {
        return attr(null, attr, value);
    }

    public final static String attr(String prefix, String attr, String value) {
        StringBuilder sb = new StringBuilder();
        if (prefix != null) {
            sb.append(prefix);
        }
        sb.append("[").append(attr);
        if (value != null) {
            sb.append("=").append(value);
        }
        sb.append("]");
        return sb.toString();
    }

    public final static String id(String id) {
        return id(null, id);
    }

    public final static String id(String prefix, String id) {
        StringBuilder sb = new StringBuilder();
        if (prefix != null) {
            sb.append(prefix);
        }
        sb.append("#").append(id);
        return sb.toString();
    }

    public final static String tag(String tag) {
        return tag.replace(':', '|');
    }
}
