package com.astamuse.asta4d.util.i18n.pattern;

import java.util.Locale;

public interface MessagePatternRetriever {

    /**
     * 
     * @param locale
     * @param key
     * @return null if no message pattern for the given key can be found
     */
    public String retrieve(Locale locale, String key);
}
