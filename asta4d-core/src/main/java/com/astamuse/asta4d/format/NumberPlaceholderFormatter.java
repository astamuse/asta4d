package com.astamuse.asta4d.format;

import java.text.MessageFormat;

import com.astamuse.asta4d.util.InvalidMessageException;

public class NumberPlaceholderFormatter extends ParamOrderDependentFormatter {

    @Override
    public String format(String pattern, Object... params) throws InvalidMessageException {
        try {
            return MessageFormat.format(pattern, params);
        } catch (IllegalArgumentException e) {
            throw new InvalidMessageException(e);
        }
    }
}
