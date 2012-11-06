package com.astamuse.asta4d.i18n;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface ResourceBundleManager {

    void setResourceNames(List<String> resourceNames);

    void setFormatter(PlaceholderFormatter formatter);

    String getString(Locale locale, String key, Map<String, String> paramMap, List<String> externalizeParamKeys)
            throws InvalidMessageException;
}
