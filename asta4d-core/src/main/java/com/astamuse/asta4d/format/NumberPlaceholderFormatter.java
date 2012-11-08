package com.astamuse.asta4d.format;

import java.text.MessageFormat;

public class NumberPlaceholderFormatter extends ParamOrderDependentFormatter {

    @Override
    public String format(String pattern, Object... params) {
        return MessageFormat.format(pattern, params);
    }
}
