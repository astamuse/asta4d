package com.astamuse.asta4d.util;

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
import com.astamuse.asta4d.util.ResourceBundleHelperBase.ParamMapResourceBundleHelper;
import com.astamuse.asta4d.util.ResourceBundleHelperBase.ResourceBundleHelper;

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

    public static ResourceBundleHelper getHelper() {
        return new ResourceBundleHelper();
    }

    public static ParamMapResourceBundleHelper getParamMapHelper() {
        return new ParamMapResourceBundleHelper();
    }

    private static ResourceBundle getResourceBundle(String resourceName, Locale locale) {
        if (locale != null || LocaleUtils.isAvailableLocale(locale)) {
            return ResourceBundle.getBundle(resourceName, locale, DEFAULT_LOCALE_EXCLUDE_CONTROL);
        }
        Locale currentLocale = Context.getCurrentThreadContext().getCurrentLocale();
        if (currentLocale != null || LocaleUtils.isAvailableLocale(currentLocale)) {
            return ResourceBundle.getBundle(resourceName, currentLocale, DEFAULT_LOCALE_EXCLUDE_CONTROL);
        }
        return ResourceBundle.getBundle(resourceName);
    }

    private ResourceBundleUtil() {
    }
}
