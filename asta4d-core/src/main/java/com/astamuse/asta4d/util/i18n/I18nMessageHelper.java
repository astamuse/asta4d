package com.astamuse.asta4d.util.i18n;

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

}
