package com.astamuse.asta4d.util.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.astamuse.asta4d.util.i18n.formatter.ApacheStrSubstitutorFormatter;
import com.astamuse.asta4d.util.i18n.formatter.MappedValueFormatter;

public class MappedParamI18nMessageHelper extends I18nMessageHelper {

    private MappedValueFormatter formatter;

    public MappedParamI18nMessageHelper() {
        this(new ApacheStrSubstitutorFormatter());
    }

    public MappedParamI18nMessageHelper(MappedValueFormatter formatter) {
        this.formatter = formatter;
    }

    public MappedValueFormatter getFormatter() {
        return formatter;
    }

    @Override
    public String getMessage(String key) {
        return getMessageInternal(null, key, null, null);
    }

    @Override
    public String getMessage(Locale locale, String key) {
        return getMessageInternal(locale, key, null, null);
    }

    public String getMessage(String key, Map<String, Object> paramMap) {
        return getMessageInternal(null, key, null, paramMap);
    }

    @SuppressWarnings("rawtypes")
    public String getMessage(String key, Pair... params) {
        return getMessageInternal(null, key, null, pairToMap(params));
    }

    public String getMessage(Locale locale, String key, Map<String, Object> paramMap) {
        return getMessageInternal(locale, key, null, paramMap);
    }

    @SuppressWarnings("rawtypes")
    public String getMessage(Locale locale, String key, Pair... params) {
        return getMessageInternal(locale, key, null, pairToMap(params));
    }

    @Override
    public String getMessageWithDefault(String key, Object defaultPattern) {
        return getMessageInternal(null, key, defaultPattern, null);
    }

    @Override
    public String getMessageWithDefault(Locale locale, String key, Object defaultPattern) {
        return getMessageInternal(locale, key, defaultPattern, null);
    }

    public String getMessageWithDefault(String key, Object defaultPattern, Map<String, Object> paramMap) {
        return getMessageInternal(null, key, defaultPattern, paramMap);
    }

    @SuppressWarnings("rawtypes")
    public String getMessageWithDefault(String key, Object defaultPattern, Pair... params) {
        return getMessageInternal(null, key, defaultPattern, pairToMap(params));
    }

    public String getMessageWithDefault(Locale locale, String key, Object defaultPattern, Map<String, Object> paramMap) {
        return getMessageInternal(locale, key, defaultPattern, paramMap);
    }

    @SuppressWarnings("rawtypes")
    public String getMessageWithDefault(Locale locale, String key, Object defaultPattern, Pair... params) {
        return getMessageInternal(locale, key, defaultPattern, pairToMap(params));
    }

    private String getMessageInternal(Locale locale, String key, Object defaultPattern, Map<String, Object> paramMap) {
        String pattern = getMessagePatternRetriever().retrieve(locale, key);

        if (pattern == null) {
            pattern = defaultPattern == null ? key : defaultPattern.toString();
        }

        if (StringUtils.isEmpty(pattern)) {
            return "";
        } else {
            return formatter.format(pattern, paramMap);
        }
    }

    @SuppressWarnings("rawtypes")
    private Map<String, Object> pairToMap(Pair[] params) {
        Map<String, Object> map = new HashMap<>();
        for (Pair pair : params) {
            map.put(pair.getKey().toString(), pair.getValue());
        }
        return map;
    }
}
