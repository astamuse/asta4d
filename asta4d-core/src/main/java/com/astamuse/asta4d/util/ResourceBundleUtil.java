package com.astamuse.asta4d.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang3.LocaleUtils;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.format.ParamOrderDependentFormatter;
import com.astamuse.asta4d.format.PlaceholderFormatter;

public class ResourceBundleUtil {

    private static final ResourceBundle.Control DEFAULT_LOCALE_EXCLUDE_CONTROL = new ResourceBundle.Control() {
        @Override
        public Locale getFallbackLocale(String paramString, Locale paramLocale) {
            // for not use default locale
            return null;
        }
    };

    public static String getString(ResourceBundleConfig<ParamOrderDependentFormatter> config, String key, ParamValue... params)
            throws InvalidMessageException {
        List<Object> paramList = new ArrayList<>();
        for (ParamValue paramEntry : params) {
            paramList.add(paramEntry.getValue(config, key));
        }
        return getString(config, key, paramList.toArray());
    }

    public static String getString(ResourceBundleConfig<ParamOrderDependentFormatter> config, String key, Object... params)
            throws InvalidMessageException {
        List<String> resourceNames = config.getResourceNames();
        MissingResourceException ex = null;
        for (String resourceName : resourceNames) {
            try {
                ResourceBundle resourceBundle = getResourceBundle(resourceName, config.getLocale());
                return config.getFormatter().format(resourceBundle.getString(key), params);
            } catch (MissingResourceException e) {
                ex = e;
            }
        }
        throw new InvalidMessageException("key[" + key + "] not found.", ex);
    }

    public static String getString(ResourceBundleConfig<PlaceholderFormatter> config, String key) throws InvalidMessageException {
        return getString(config, key, Collections.<String, Object> emptyMap());
    }

    public static String getString(ResourceBundleConfig<PlaceholderFormatter> config, String key,
            @SuppressWarnings("unchecked") Entry<String, ParamValue>... paramEntries) throws InvalidMessageException {
        Map<String, Object> paramMap = new HashMap<>();
        for (Entry<String, ParamValue> paramEntry : paramEntries) {
            paramMap.put(paramEntry.getKey(), paramEntry.getValue().getValue(config, key));
        }
        return getString(config, key, paramMap);
    }

    public static String getString(ResourceBundleConfig<PlaceholderFormatter> config, String key, Map<String, Object> paramMap)
            throws InvalidMessageException {
        List<String> resourceNames = config.getResourceNames();
        MissingResourceException ex = null;
        for (String resourceName : resourceNames) {
            try {
                ResourceBundle resourceBundle = getResourceBundle(resourceName, config.getLocale());
                return config.getFormatter().format(resourceBundle.getString(key), paramMap);
            } catch (MissingResourceException e) {
                ex = e;
            }
        }
        throw new InvalidMessageException("key[" + key + "] not found.", ex);
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

    public static class ResourceBundleConfig<T extends PlaceholderFormatter> {

        private final T formatter;
        private List<String> resourceNames = new ArrayList<>();
        private Locale locale;

        public ResourceBundleConfig(T formatter) {
            this.formatter = formatter;
        }

        public ResourceBundleConfig(T formatter, List<String> resourceNames, Locale locale) {
            this.formatter = formatter;
            this.resourceNames = resourceNames;
            this.locale = locale;
        }

        public List<String> getResourceNames() {
            return new ArrayList<>(resourceNames);
        }

        public void setResourceName(List<String> resourceNames) {
            this.resourceNames = resourceNames;
        }

        public void setResourceName(String... resourceNames) {
            setResourceName(Arrays.asList(resourceNames));
        }

        public void addResourceName(String... resourceNames) {
            for (String resourceName : resourceNames) {
                this.resourceNames.add(resourceName);
            }
        }

        public Locale getLocale() {
            if (locale == null) {
                locale = Context.getCurrentThreadContext().getCurrentLocale();
            }
            return locale;
        }

        public void setLocale(Locale locale) {
            this.locale = locale;
        }

        public T getFormatter() {
            return formatter;
        }
    }

    public abstract static class ParamValue {
        private final Object value;

        public ParamValue(Object value) {
            this.value = value;
        }

        public Object getValue(ResourceBundleConfig<?> config, String key) throws InvalidMessageException {
            return value;
        }
    }

    public static class InternalParamValue extends ParamValue {
        public InternalParamValue(Object value) {
            super(value);
        }
    }

    public static class ExternalParamValue extends ParamValue {
        public ExternalParamValue(Object value) {
            super(value);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public Object getValue(ResourceBundleConfig config, String key) throws InvalidMessageException {
            return getString(config, key + '.' + super.getValue(config, key));
        }
    }
}
