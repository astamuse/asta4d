package com.astamuse.asta4d.format;


public class SymbolPlaceholderFormatter extends ParamOrderDependentFormatter {

    @Override
    public String format(String pattern, Object... params) {
        return String.format(pattern, params);
    }
}
