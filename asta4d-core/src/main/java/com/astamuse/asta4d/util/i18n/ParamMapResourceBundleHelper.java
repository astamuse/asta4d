package com.astamuse.asta4d.util.i18n;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import com.astamuse.asta4d.format.PlaceholderFormatter;
import com.astamuse.asta4d.util.InvalidMessageException;

public class ParamMapResourceBundleHelper extends ResourceBundleHelperBase {

    public ParamMapResourceBundleHelper() {
        super();
    }

    public ParamMapResourceBundleHelper(Locale locale, PlaceholderFormatter formatter) {
        super(locale, formatter);
    }

    public ParamMapResourceBundleHelper(Locale locale) {
        super(locale);
    }

    public ParamMapResourceBundleHelper(PlaceholderFormatter formatter) {
        super(formatter);
    }

    public String getMessage(String key) throws InvalidMessageException {
        return ResourceBundleUtil.getMessage(getFormatter(), getLocale(), key, Collections.<String, Object> emptyMap());
    }

    public String getMessage(String key, Map<String, Object> paramMap) throws InvalidMessageException {
        return ResourceBundleUtil.getMessage(getFormatter(), getLocale(), key, paramMap);
    }

}
