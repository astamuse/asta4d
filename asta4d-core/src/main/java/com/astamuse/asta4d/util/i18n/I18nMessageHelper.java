package com.astamuse.asta4d.util.i18n;

import java.util.Locale;

import com.astamuse.asta4d.util.i18n.pattern.JDKResourceBundleMessagePatternRetriever;
import com.astamuse.asta4d.util.i18n.pattern.MessagePatternRetriever;

public abstract class I18nMessageHelper {

    private MessagePatternRetriever messagePatternRetriever;

    public I18nMessageHelper() {
        this(new JDKResourceBundleMessagePatternRetriever());
    }

    public I18nMessageHelper(MessagePatternRetriever messagePatternRetriever) {
        this.messagePatternRetriever = messagePatternRetriever;
    }

    public MessagePatternRetriever getMessagePatternRetriever() {
        return messagePatternRetriever;
    }

    public void setMessagePatternRetriever(MessagePatternRetriever messagePatternRetriever) {
        this.messagePatternRetriever = messagePatternRetriever;
    }

    public abstract String getMessage(String key);

    public abstract String getMessage(Locale locale, String key);

    public abstract String getMessageWithDefault(String key, Object defaultPattern);

    public abstract String getMessageWithDefault(Locale locale, String key, Object defaultPattern);
}
