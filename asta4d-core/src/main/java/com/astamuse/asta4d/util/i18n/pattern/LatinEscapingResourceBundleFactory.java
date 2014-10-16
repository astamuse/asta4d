package com.astamuse.asta4d.util.i18n.pattern;

import java.util.Locale;
import java.util.ResourceBundle;

public class LatinEscapingResourceBundleFactory implements ResourceBundleFactory {

    @Override
    public ResourceBundle retrieveResourceBundle(String baseName, Locale locale) {
        return ResourceBundle.getBundle(baseName, locale == null ? Locale.getDefault() : locale);
    }

}
