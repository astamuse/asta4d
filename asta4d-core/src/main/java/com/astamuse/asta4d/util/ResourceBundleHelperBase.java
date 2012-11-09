package com.astamuse.asta4d.util;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.format.ParamOrderDependentFormatter;
import com.astamuse.asta4d.format.PlaceholderFormatter;
import com.astamuse.asta4d.format.SymbolPlaceholderFormatter;

public abstract class ResourceBundleHelperBase<T extends ResourceBundleHelperBase<?>> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ResourceBundleHelperBase.class);

    private Locale locale = Context.getCurrentThreadContext().getCurrentLocale();

    @SuppressWarnings("unchecked")
    public T locale(Locale locale) {
        this.locale = locale;
        return (T) this;
    }

    protected Locale getLocale() {
        return locale;
    }

    protected abstract PlaceholderFormatter getFormatter();

    public ExternalParamValue getExternalParamValue(String key, Object value) {
        return new ExternalParamValue(getFormatter(), locale, key, value);
    }

    public static class ResourceBundleHelper extends ResourceBundleHelperBase<ResourceBundleHelper> {
        private ParamOrderDependentFormatter formatter;

        ResourceBundleHelper() {
            PlaceholderFormatter formatter = Context.getCurrentThreadContext().getConfiguration().getPlaceholderFormatter();
            if (formatter instanceof ParamOrderDependentFormatter) {
                this.formatter = (ParamOrderDependentFormatter) formatter;
            } else {
                this.formatter = new SymbolPlaceholderFormatter();
            }
        }

        public ResourceBundleHelper formatter(ParamOrderDependentFormatter formatter) {
            this.formatter = formatter;
            return this;
        }

        public String getMessage(String key, Object... params) throws InvalidMessageException {
            return ResourceBundleUtil.getMessage(getFormatter(), getLocale(), key, params);
        }

        @Override
        protected ParamOrderDependentFormatter getFormatter() {
            return formatter;
        }
    }

    public static class ParamMapResourceBundleHelper extends ResourceBundleHelperBase<ParamMapResourceBundleHelper> {
        private PlaceholderFormatter formatter = Context.getCurrentThreadContext().getConfiguration().getPlaceholderFormatter();

        ParamMapResourceBundleHelper() {
        }

        public ParamMapResourceBundleHelper formatter(PlaceholderFormatter formatter) {
            this.formatter = formatter;
            return this;
        }

        public String getMessage(String key) throws InvalidMessageException {
            return ResourceBundleUtil.getMessage(getFormatter(), getLocale(), key, Collections.<String, Object> emptyMap());
        }

        public String getMessage(String key, Map<String, Object> paramMap) throws InvalidMessageException {
            return ResourceBundleUtil.getMessage(getFormatter(), getLocale(), key, paramMap);
        }

        @Override
        protected PlaceholderFormatter getFormatter() {
            return formatter;
        }
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
