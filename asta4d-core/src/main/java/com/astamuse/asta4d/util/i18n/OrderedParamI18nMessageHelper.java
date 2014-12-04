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

import java.text.MessageFormat;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.util.i18n.formatter.JDKMessageFormatFormatter;
import com.astamuse.asta4d.util.i18n.formatter.OrderedValueFormatter;

/**
 * Allow format message by a given parameter array. A {@link OrderedValueFormatter} is required to supply concrete formatting style and the
 * default is {@link JDKMessageFormatFormatter} which uses JDK's {@link MessageFormat} to format message string.
 * <p>
 * If message is not found for given key(and locale), the key will be treated as default message if the default message is not specified.
 * 
 * @author e-ryu
 *
 */
public class OrderedParamI18nMessageHelper extends I18nMessageHelper {

    private OrderedValueFormatter formatter;

    public OrderedParamI18nMessageHelper() {
        this(new JDKMessageFormatFormatter());
    }

    public OrderedParamI18nMessageHelper(OrderedValueFormatter formatter) {
        this.formatter = formatter;
    }

    public OrderedValueFormatter getFormatter() {
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
     * Retrieve message by given key and format it by given parameter array.
     * 
     * @param key
     * @param params
     * @return
     */
    public String getMessage(String key, Object... params) {
        return getMessageInternal(null, key, null, params);
    }

    /**
     * Retrieve message by given locale and key and format it by given parameter array.
     * 
     * @param locale
     * @param key
     * @param params
     * @return
     */
    public String getMessage(Locale locale, String key, Object... params) {
        return getMessageInternal(locale, key, null, params);
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
     * Retrieve message by given key and format it by given parameter array.If message is not found, defaultPattern#toString will be used to
     * generate a default message pattern to be formatted.
     * 
     * @param key
     * @param defaultPattern
     * @param params
     * @return
     */
    public String getMessageWithDefault(String key, Object defaultPattern, Object... params) {
        return getMessageInternal(null, key, defaultPattern, params);
    }

    /**
     * Retrieve message by given locale and key and format it by given parameter array.If message is not found, defaultPattern#toString will
     * be used to generate a default message pattern to be formatted.
     * 
     * @param locale
     * @param key
     * @param defaultPattern
     * @param params
     * @return
     */
    public String getMessageWithDefault(Locale locale, String key, Object defaultPattern, Object... params) {
        return getMessageInternal(locale, key, defaultPattern, params);
    }

    private String getMessageInternal(Locale locale, String key, Object defaultPattern, Object[] params) {
        String pattern = getMessagePatternRetriever().retrieve(locale, key);

        if (pattern == null) {
            pattern = defaultPattern == null ? key : defaultPattern.toString();
        }

        if (StringUtils.isEmpty(pattern)) {
            return "";
        } else {
            return formatter.format(pattern, params);
        }
    }

}
