package com.astamuse.asta4d.format;

import java.util.MissingFormatArgumentException;

import com.astamuse.asta4d.util.InvalidMessageException;

public class SymbolPlaceholderFormatter extends ParamOrderDependentFormatter {

    @Override
    public String format(String pattern, Object... params) throws InvalidMessageException {
        try {
            return String.format(pattern, params);
        } catch (MissingFormatArgumentException e) {
            throw new InvalidMessageException(e);
        }
    }
}
