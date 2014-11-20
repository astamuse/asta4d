package com.astamuse.asta4d.util.i18n.pattern;

import java.util.Locale;

import com.astamuse.asta4d.util.i18n.I18nMessageHelper;

/**
 * A retriever used by {@link I18nMessageHelper} to retrieve message
 * 
 * @author e-ryu
 *
 */
public interface MessagePatternRetriever {

    /**
     * 
     * @param locale
     * @param key
     * @return null if no message pattern for the given key can be found
     */
    public String retrieve(Locale locale, String key);
}
