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

package com.astamuse.asta4d.util.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

public class LocalizeUtil {

    public static String[] getCandidatePaths(String path, Locale locale) {
        int dotIndex = path.lastIndexOf(".");
        String name = dotIndex > 0 ? path.substring(0, dotIndex) : path;
        String extension = dotIndex > 0 ? path.substring(dotIndex) : StringUtils.EMPTY;
        List<String> candidatePathList = new ArrayList<>();
        if (!StringUtils.isEmpty(locale.getCountry())) {
            candidatePathList.add(name + '_' + locale.toString() + extension);
        }
        if (!StringUtils.isEmpty(locale.getLanguage())) {
            candidatePathList.add(name + '_' + locale.getLanguage() + extension);
        }
        candidatePathList.add(path);
        return candidatePathList.toArray(new String[candidatePathList.size()]);
    }

    public static String createLocalizedKey(String str, Locale locale) {
        if (StringUtils.isEmpty(locale.toLanguageTag())) {
            return str;
        }
        return str + "::" + locale.toLanguageTag();
    }

    public static Locale getLocale(String localeStr) {
        if (StringUtils.isEmpty(localeStr)) {
            return null;
        }
        try {
            return LocaleUtils.toLocale(localeStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private LocalizeUtil() {
    }
}
