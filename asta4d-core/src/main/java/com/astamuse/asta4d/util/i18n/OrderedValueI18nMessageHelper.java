package com.astamuse.asta4d.util.i18n;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.util.i18n.formatter.JDKMessageFormatFormatter;
import com.astamuse.asta4d.util.i18n.formatter.OrderedValueFormatter;

public class OrderedValueI18nMessageHelper extends I18nMessageHelper {

    private OrderedValueFormatter formatter;

    public OrderedValueI18nMessageHelper() {
        this(new JDKMessageFormatFormatter());
    }

    public OrderedValueI18nMessageHelper(OrderedValueFormatter formatter) {
        this.formatter = formatter;
    }

    public OrderedValueFormatter getFormatter() {
        return formatter;
    }

    public String getMessage(String key, Object... params) {
        return getMessageWithDefault(null, key, null, params);
    }

    public String getMessage(Locale locale, String key, Object... params) {
        return getMessageWithDefault(locale, key, null, params);
    }

    public String getMessageWithDefault(String key, Object defaultPattern, Object... params) {
        return getMessageWithDefault(null, key, defaultPattern, params);
    }

    public String getMessageWithDefault(Locale locale, String key, Object defaultPattern, Object... params) {
        String pattern = getMessagePatternRetriever().retrieve(locale, key);

        if (pattern == null) {
            pattern = defaultPattern == null ? key : defaultPattern.toString();
        }

        if (StringUtils.isEmpty(pattern)) {
            return "";
        } else {
            return formatter.format(pattern, params);
        }
    }

}
