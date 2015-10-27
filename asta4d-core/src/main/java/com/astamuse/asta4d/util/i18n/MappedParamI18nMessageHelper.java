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
package com.astamuse.asta4d.util.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.astamuse.asta4d.util.i18n.formatter.ApacheStrSubstitutorFormatter;
import com.astamuse.asta4d.util.i18n.formatter.MappedValueFormatter;

/**
 * Allow format message by a given parameter map, A {@link MappedValueFormatter} is required to supply concrete formatting style and the
 * default is {@link ApacheStrSubstitutorFormatter} which uses StrSubstitutor from Apache Common lang3.
 * <p>
 * 
 * If message is not found for given key(and locale), the key will be treated as default message if the default message is not specified.
 * 
 * @author e-ryu
 * @see ApacheStrSubstitutorFormatter
 */
public class MappedParamI18nMessageHelper extends I18nMessageHelper {

    private MappedValueFormatter formatter;

    public MappedParamI18nMessageHelper() {
        this(new ApacheStrSubstitutorFormatter());
    }

    public MappedParamI18nMessageHelper(MappedValueFormatter formatter) {
        this.formatter = formatter;
    }

    public MappedValueFormatter getFormatter() {
        return formatter;
    }

    @Override
    public String getMessage(String key) {
        return getMessageInternal(null, key, null, null);
    }

    @Override
    public String getMessage(Locale locale, String key) {
        return getMessageInternal(locale, key, null, null);
    }

    /**
     * Retrieve message by given key and format it by given parameter map.
     * 
     * @param key
     * @param paramMap
     * @return
     */
    public String getMessage(String key, Map<String, Object> paramMap) {
        return getMessageInternal(null, key, null, paramMap);
    }

    /**
     * Retrieve message by given key and format it by given parameter pairs.
     * 
     * @param key
     * @param params
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getMessage(String key, Pair... params) {
        return getMessageInternal(null, key, null, pairToMap(params));
    }

    /**
     * Retrieve message by given locale and key and format it by given parameter map.
     * 
     * @param locale
     * @param key
     * @param paramMap
     * @return
     */
    public String getMessage(Locale locale, String key, Map<String, Object> paramMap) {
        return getMessageInternal(locale, key, null, paramMap);
    }

    /**
     * Retrieve message by given locale and key and format it by given parameter pairs.
     * 
     * @param locale
     * @param key
     * @param params
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getMessage(Locale locale, String key, Pair... params) {
        return getMessageInternal(locale, key, null, pairToMap(params));
    }

    @Override
    public String getMessageWithDefault(String key, Object defaultPattern) {
        return getMessageInternal(null, key, defaultPattern, null);
    }

    @Override
    public String getMessageWithDefault(Locale locale, String key, Object defaultPattern) {
        return getMessageInternal(locale, key, defaultPattern, null);
    }

    /**
     * Retrieve message by given key and format it by given parameter map. If message is not found, defaultPattern#toString will be used to
     * generate a default message pattern to be formatted.
     * 
     * @param key
     * @param defaultPattern
     * @param paramMap
     * @return
     */
    public String getMessageWithDefault(String key, Object defaultPattern, Map<String, Object> paramMap) {
        return getMessageInternal(null, key, defaultPattern, paramMap);
    }

    /**
     * Retrieve message by given key and format it by given parameter pairs. If message is not found, defaultPattern#toString will be used
     * to generate a default message pattern to be formatted.
     * 
     * @param key
     * @param defaultPattern
     * @param params
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getMessageWithDefault(String key, Object defaultPattern, Pair... params) {
        return getMessageInternal(null, key, defaultPattern, pairToMap(params));
    }

    /**
     * Retrieve message by given locale and key and format it by given parameter map. If message is not found, defaultPattern#toString will
     * be used to generate a default message pattern to be formatted.
     * 
     * @param locale
     * @param key
     * @param defaultPattern
     * @param paramMap
     * @return
     */
    public String getMessageWithDefault(Locale locale, String key, Object defaultPattern, Map<String, Object> paramMap) {
        return getMessageInternal(locale, key, defaultPattern, paramMap);
    }

    /**
     * Retrieve message by given locale and key and format it by given parameter pairs. If message is not found, defaultPattern#toString
     * will be used to generate a default message pattern to be formatted.
     * 
     * @param locale
     * @param key
     * @param defaultPattern
     * @param params
     * @return
     */
    @SuppressWarnings("rawtypes")
    public String getMessageWithDefault(Locale locale, String key, Object defaultPattern, Pair... params) {
        return getMessageInternal(locale, key, defaultPattern, pairToMap(params));
    }

    protected String getMessageInternal(Locale locale, String key, Object defaultPattern, Map<String, Object> paramMap) {
        String pattern = getMessagePatternRetriever().retrieve(locale, key);

        if (pattern == null) {
            pattern = defaultPattern == null ? key : defaultPattern.toString();
        }

        if (StringUtils.isEmpty(pattern)) {
            return "";
        } else {
            return formatter.format(pattern, paramMap);
        }
    }

    @SuppressWarnings("rawtypes")
    private Map<String, Object> pairToMap(Pair[] params) {
        Map<String, Object> map = new HashMap<>();
        for (Pair pair : params) {
            map.put(pair.getKey().toString(), pair.getValue());
        }
        return map;
    }
}
