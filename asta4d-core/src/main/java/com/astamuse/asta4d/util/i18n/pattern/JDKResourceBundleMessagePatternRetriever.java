package com.astamuse.asta4d.util.i18n.pattern;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;

public class JDKResourceBundleMessagePatternRetriever implements MessagePatternRetriever {

    private ResourceBundleFactory resourceBundleFactory = new CharsetResourceBundleFactory();

    private List<String> resourceNames = new LinkedList<>();

    public ResourceBundleFactory getResourceBundleFactory() {
        return resourceBundleFactory;
    }

    public void setResourceBundleFactory(ResourceBundleFactory resourceBundleFactory) {
        this.resourceBundleFactory = resourceBundleFactory;
    }

    public void setResourceNames(List<String> resourceNames) {
        this.resourceNames = new LinkedList<>(resourceNames);
    }

    @Override
    public String retrieve(Locale locale, String key) {
        String pattern = null;
        for (String resourceName : resourceNames) {
            try {
                ResourceBundle resourceBundle = getResourceBundle(resourceName, locale);
                pattern = resourceBundle.getString(key);
            } catch (MissingResourceException e) {
                //
            }
        }
        return pattern;
    }

    protected ResourceBundle getResourceBundle(String resourceName, Locale locale) {
        Configuration config = Configuration.getConfiguration();

        if (!config.isCacheEnable()) {
            ResourceBundle.clearCache();
        }

        if (locale == null) {
            locale = Context.getCurrentThreadContext().getCurrentLocale();
            if (locale == null) {
                locale = Locale.getDefault();
            }
        }
        return resourceBundleFactory.retrieveResourceBundle(resourceName, locale);
    }
}
