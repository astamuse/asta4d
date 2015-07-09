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
package com.astamuse.asta4d.web.form;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class CascadeFormUtil {

    public static final int[] EMPTY_INDEXES = new int[0];

    private static final String[] PlaceHolderSearchKey = new String[100];
    static {
        for (int i = 0; i < PlaceHolderSearchKey.length; i++) {
            PlaceHolderSearchKey[i] = StringUtils.repeat("@", i + 1);
        }
    }

    public static final String rewriteArrayIndexPlaceHolder(String s, int[] indexes) {
        String ret = s;
        for (int i = indexes.length - 1; i >= 0; i--) {
            ret = StringUtils.replace(ret, PlaceHolderSearchKey[i], String.valueOf(indexes[i]));
        }
        return ret;
    }

    public static final int[] addIndex(int[] indexes, int index) {
        return ArrayUtils.add(indexes, Integer.valueOf(index));
    }
}
