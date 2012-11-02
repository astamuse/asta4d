package com.astamuse.asta4d.i18n;

import java.util.Map;

public interface PlaceholderFormatter {

    public String format(String pattern, Map<String, Object> paramMap);
}
