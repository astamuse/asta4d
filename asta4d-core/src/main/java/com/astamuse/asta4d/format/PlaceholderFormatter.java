package com.astamuse.asta4d.format;

import java.util.Map;

public interface PlaceholderFormatter {

    String format(String pattern, Map<String, Object> paramMap);
}
