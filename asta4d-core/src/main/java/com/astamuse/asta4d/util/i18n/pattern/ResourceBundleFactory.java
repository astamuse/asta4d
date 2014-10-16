package com.astamuse.asta4d.util.i18n.pattern;

import java.util.Locale;
import java.util.ResourceBundle;

public interface ResourceBundleFactory {
    public ResourceBundle retrieveResourceBundle(String baseName, Locale locale);
}