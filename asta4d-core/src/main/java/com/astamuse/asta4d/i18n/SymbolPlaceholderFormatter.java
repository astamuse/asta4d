package com.astamuse.asta4d.i18n;

import java.util.List;
import java.util.Map;

public class SymbolPlaceholderFormatter implements PlaceholderFormatter {

    @Override
    public String format(String pattern, Map<String, Object> paramMap) {
        List<Object> params = MessagesUtil.retrieveNumberedParamKeyList(paramMap);
        return String.format(pattern, params.toArray());
    }
}
