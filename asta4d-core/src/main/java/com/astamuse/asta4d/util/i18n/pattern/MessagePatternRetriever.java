package com.astamuse.asta4d.util.i18n.pattern;

import java.util.Locale;

public interface MessagePatternRetriever {
    public String retrieve(Locale locale, String key);
}
