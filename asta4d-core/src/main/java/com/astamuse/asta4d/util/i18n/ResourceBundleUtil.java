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

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang3.LocaleUtils;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.format.ParamOrderDependentFormatter;
import com.astamuse.asta4d.format.PlaceholderFormatter;
import com.astamuse.asta4d.util.InvalidMessageException;

public class ResourceBundleUtil {

    private static final ResourceBundle.Control DEFAULT_LOCALE_EXCLUDE_CONTROL = new ResourceBundle.Control() {
        @Override
        public Locale getFallbackLocale(String paramString, Locale paramLocale) {
            // for not use default locale
            return null;
        }
    };

    public static String getMessage(ParamOrderDependentFormatter formatter, Locale locale, String key, Object... params)
            throws InvalidMessageException {
        List<String> resourceNames = Context.getCurrentThreadContext().getConfiguration().getResourceNames();
        MissingResourceException ex = null;
        for (String resourceName : resourceNames) {
            try {
                ResourceBundle resourceBundle = getResourceBundle(resourceName, locale);
                return formatter.format(resourceBundle.getString(key), params);
            } catch (MissingResourceException e) {
                ex = e;
            }
        }
        throw new InvalidMessageException("key[" + key + "] not found.", ex);
    }

    public static String getMessage(PlaceholderFormatter formatter, Locale locale, String key) throws InvalidMessageException {
        return getMessage(formatter, locale, key, Collections.<String, Object> emptyMap());
    }

    public static String getMessage(PlaceholderFormatter formatter, Locale locale, String key, Map<String, Object> paramMap)
            throws InvalidMessageException {
        List<String> resourceNames = Context.getCurrentThreadContext().getConfiguration().getResourceNames();
        MissingResourceException ex = null;
        for (String resourceName : resourceNames) {
            try {
                ResourceBundle resourceBundle = getResourceBundle(resourceName, locale);
                return formatter.format(resourceBundle.getString(key), paramMap);
            } catch (MissingResourceException e) {
                ex = e;
            }
        }
        throw new InvalidMessageException("key[" + key + "] not found.", ex);
    }

    private static ResourceBundle getResourceBundle(String resourceName, Locale locale) {
        Context context = Context.getCurrentThreadContext();
        if (!context.getConfiguration().isCacheEnable()) {
            ResourceBundle.clearCache();
        }
        if (locale != null || LocaleUtils.isAvailableLocale(locale)) {
            return ResourceBundle.getBundle(resourceName, locale);
        }
        Locale currentLocale = context.getCurrentLocale();
        if (currentLocale != null || LocaleUtils.isAvailableLocale(currentLocale)) {
            return ResourceBundle.getBundle(resourceName, currentLocale);
        }
        return ResourceBundle.getBundle(resourceName);
    }

    private ResourceBundleUtil() {
    }
}
