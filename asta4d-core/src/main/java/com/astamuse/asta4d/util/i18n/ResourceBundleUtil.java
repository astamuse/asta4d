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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.util.i18n.format.ParamOrderDependentFormatter;
import com.astamuse.asta4d.util.i18n.format.PlaceholderFormatter;

public class ResourceBundleUtil {

    private static final ResourceBundle.Control DEFAULT_LOCALE_EXCLUDE_CONTROL = new ResourceBundle.Control() {
        @Override
        public Locale getFallbackLocale(String paramString, Locale paramLocale) {
            // for not use default locale
            return null;
        }
    };

    public static String getMessage(ParamOrderDependentFormatter formatter, Locale locale, String key, String defaultMsg, Object... params) {
        List<String> resourceNames = Configuration.getConfiguration().getResourceNames();
        MissingResourceException ex = null;
        String pattern = null;
        for (String resourceName : resourceNames) {
            try {
                ResourceBundle resourceBundle = getResourceBundle(resourceName, locale);
                pattern = resourceBundle.getString(key);

            } catch (MissingResourceException e) {
                // ex = e;
            }
        }
        if (pattern == null) {
            pattern = defaultMsg != null ? defaultMsg : key;
        }

        if (StringUtils.isEmpty(pattern)) {
            return "";
        }

        if (params == null || params.length == 0) {
            return pattern;
        } else {
            return formatter.format(pattern, params);
        }

    }

    public static String getMessage(PlaceholderFormatter formatter, Locale locale, String key, String defaultMsg,
            Map<String, Object> paramMap) {
        List<String> resourceNames = Configuration.getConfiguration().getResourceNames();
        MissingResourceException ex = null;
        String pattern = null;
        for (String resourceName : resourceNames) {
            try {
                ResourceBundle resourceBundle = getResourceBundle(resourceName, locale);
                pattern = resourceBundle.getString(key);
            } catch (MissingResourceException e) {
                ex = e;
            }
        }
        if (pattern == null) {
            pattern = defaultMsg != null ? defaultMsg : key;
        }

        if (StringUtils.isEmpty(pattern)) {
            return "";
        }

        if (paramMap == null || paramMap.isEmpty()) {
            return pattern;
        } else {
            return formatter.format(pattern, paramMap);
        }

    }

    private static ResourceBundle getResourceBundle(String resourceName, Locale locale) {
        Configuration config = Configuration.getConfiguration();

        if (!config.isCacheEnable()) {
            ResourceBundle.clearCache();
        }

        ResourceBundleFactory factory = config.getResourceBundleFactory();

        if (locale != null || LocaleUtils.isAvailableLocale(locale)) {
            return factory.retrieveResourceBundle(resourceName, locale);
        }

        Context context = Context.getCurrentThreadContext();
        Locale currentLocale = context.getCurrentLocale();
        if (currentLocale != null || LocaleUtils.isAvailableLocale(currentLocale)) {
            return factory.retrieveResourceBundle(resourceName, currentLocale);
        } else {
            return factory.retrieveResourceBundle(resourceName, Locale.getDefault());
        }
    }

    private ResourceBundleUtil() {
    }
}
