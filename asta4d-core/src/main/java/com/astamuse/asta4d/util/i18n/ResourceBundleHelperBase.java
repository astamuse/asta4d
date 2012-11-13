package com.astamuse.asta4d.util.i18n;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.format.PlaceholderFormatter;
import com.astamuse.asta4d.util.InvalidMessageException;

public abstract class ResourceBundleHelperBase {
    private final static Logger LOGGER = LoggerFactory.getLogger(ResourceBundleHelperBase.class);

    private Locale locale = Context.getCurrentThreadContext().getCurrentLocale();

    private PlaceholderFormatter formatter = Context.getCurrentThreadContext().getConfiguration().getPlaceholderFormatter();

    public ResourceBundleHelperBase(Locale locale, PlaceholderFormatter formatter) {
        this.locale = locale;
        this.formatter = formatter;
    }

    public ResourceBundleHelperBase(Locale locale) {
        this(locale, Context.getCurrentThreadContext().getConfiguration().getPlaceholderFormatter());
    }

    public ResourceBundleHelperBase(PlaceholderFormatter formatter) {
        this(Context.getCurrentThreadContext().getCurrentLocale(), formatter);
    }

    public ResourceBundleHelperBase() {
        this(Context.getCurrentThreadContext().getCurrentLocale(), Context.getCurrentThreadContext().getConfiguration()
                .getPlaceholderFormatter());
    }

    protected Locale getLocale() {
        return locale;
    }

    protected PlaceholderFormatter getFormatter() {
        return this.formatter;
    }

    public ExternalParamValue getExternalParamValue(String key, Object value) {
        return new ExternalParamValue(getFormatter(), locale, key, value);
    }

    private class ExternalParamValue {
        private final PlaceholderFormatter formatter;
        private final Locale locale;
        private final String key;
        private final Object value;

        public ExternalParamValue(PlaceholderFormatter formatter, Locale locale, String key, Object value) {
            this.value = value;
            this.formatter = formatter;
            this.locale = locale;
            this.key = key;
        }

        @Override
        public String toString() {
            String msgKey = key + '.' + value;
            try {
                return ResourceBundleUtil.getMessage(formatter, locale, msgKey);
            } catch (InvalidMessageException e) {
                LOGGER.warn("failed to get the message. key=" + msgKey, e);
                return '!' + msgKey + '!';
            }
        }
    }
}
