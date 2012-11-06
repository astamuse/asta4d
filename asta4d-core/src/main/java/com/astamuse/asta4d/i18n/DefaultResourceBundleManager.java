package com.astamuse.asta4d.i18n;

import java.util.ArrayList;
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

public class DefaultResourceBundleManager implements ResourceBundleManager {

    private static final String RESOURCE_NAME = "messages";

    private List<String> resourceNames;

    private PlaceholderFormatter formatter = new SymbolPlaceholderFormatter();

    public DefaultResourceBundleManager() {
        List<String> resourceNames = new ArrayList<>();
        resourceNames.add(RESOURCE_NAME);
        this.resourceNames = Collections.unmodifiableList(resourceNames);
    }

    @Override
    public void setResourceNames(List<String> resourceNames) {
        this.resourceNames = Collections.unmodifiableList(resourceNames);
    }

    @Override
    public void setFormatter(PlaceholderFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public String getString(Locale locale, String key, Map<String, String> paramMap, List<String> externalizeParamKeys)
            throws InvalidMessageException {
        InvalidMessageException ex = null;
        for (String resourceName : resourceNames) {
            try {
                ResourceBundle resourceBundle = getResourceBundle(resourceName, locale);
                return formatter
                        .format(resourceBundle.getString(key), getParamStrings(resourceBundle, key, paramMap, externalizeParamKeys));
            } catch (InvalidMessageException e) {
                ex = e;
                continue;
            }
        }
        throw ex;
    }

    private Map<String, Object> getParamStrings(ResourceBundle resourceBundle, String key, Map<String, String> paramMap,
            List<String> externalizeParamKeys) throws InvalidMessageException {
        if (paramMap == null || paramMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Object> messageResolvedMap = new HashMap<>();
        for (Entry<String, String> paramEntry : paramMap.entrySet()) {
            if (externalizeParamKeys.contains(paramEntry.getKey().trim())) {
                try {
                    // TODO should we call getString recursively?
                    messageResolvedMap.put(paramEntry.getKey(), resourceBundle.getString(key + "." + paramEntry.getValue()));
                } catch (MissingResourceException e) {
                    throw new InvalidMessageException("key[" + key + "." + paramEntry.getKey() + "] not found.", e);
                }
            } else {
                messageResolvedMap.put(paramEntry.getKey(), paramEntry.getValue());
            }
        }
        return messageResolvedMap;
    }

    private ResourceBundle getResourceBundle(String resourceName, Locale locale) {
        if (locale != null || LocaleUtils.isAvailableLocale(locale)) {
            return ResourceBundle.getBundle(resourceName, locale, MessagesUtil.getDefaultLocaleExcludeControl());
        }
        Locale currentLocale = Context.getCurrentThreadContext().getCurrentLocale();
        if (currentLocale != null || LocaleUtils.isAvailableLocale(currentLocale)) {
            return ResourceBundle.getBundle(resourceName, currentLocale, MessagesUtil.getDefaultLocaleExcludeControl());
        }
        return ResourceBundle.getBundle(resourceName);
    }
}
