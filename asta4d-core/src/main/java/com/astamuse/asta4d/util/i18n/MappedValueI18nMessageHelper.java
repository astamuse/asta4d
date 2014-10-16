package com.astamuse.asta4d.util.i18n;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.util.i18n.formatter.ApacheStrSubstitutorFormatter;
import com.astamuse.asta4d.util.i18n.formatter.MappedValueFormatter;

public class MappedValueI18nMessageHelper extends I18nMessageHelper {

    private MappedValueFormatter formatter;

    public MappedValueI18nMessageHelper() {
        this(new ApacheStrSubstitutorFormatter());
    }

    public MappedValueI18nMessageHelper(MappedValueFormatter formatter) {
        this.formatter = formatter;
    }

    public MappedValueFormatter getFormatter() {
        return formatter;
    }

    public String getMessage(String key) {
        return getMessageWithDefault(null, key, null, null);
    }

    public String getMessage(Locale locale, String key) {
        return getMessageWithDefault(locale, key, null, null);
    }

    public String getMessage(String key, Map<String, Object> paramMap) {
        return getMessageWithDefault(null, key, null, paramMap);
    }

    public String getMessage(Locale locale, String key, Map<String, Object> paramMap) {
        return getMessageWithDefault(locale, key, null, paramMap);
    }

    public String getMessageWithDefault(String key, Object defaultPattern, Map<String, Object> paramMap) {
        return getMessageWithDefault(null, key, defaultPattern, paramMap);
    }

    public String getMessageWithDefault(Locale locale, String key, Object defaultPattern, Map<String, Object> paramMap) {
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
}
