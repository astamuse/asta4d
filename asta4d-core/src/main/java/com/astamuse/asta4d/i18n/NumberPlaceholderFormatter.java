package com.astamuse.asta4d.i18n;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class NumberPlaceholderFormatter implements PlaceholderFormatter {

    @Override
    public String format(String pattern, Map<String, Object> paramMap) {
        List<Object> params = MessagesUtil.retrieveNumberedParamKeyList(paramMap);
        return MessageFormat.format(pattern, params.toArray());
    }
}
