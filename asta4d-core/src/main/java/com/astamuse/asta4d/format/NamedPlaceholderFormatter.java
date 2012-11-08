package com.astamuse.asta4d.format;

import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

public class NamedPlaceholderFormatter implements PlaceholderFormatter {

    private String prefix = "{";

    private String suffix = "}";

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String format(String pattern, Map<String, Object> paramMap) {
        StrSubstitutor sub = new StrSubstitutor(paramMap, prefix, suffix);
        return sub.replace(pattern);
    }
}
