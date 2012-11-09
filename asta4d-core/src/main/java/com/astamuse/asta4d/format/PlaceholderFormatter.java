package com.astamuse.asta4d.format;

import java.util.Map;

import com.astamuse.asta4d.util.InvalidMessageException;

public interface PlaceholderFormatter {

    String format(String pattern, Map<String, Object> paramMap) throws InvalidMessageException;
}
