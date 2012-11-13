package com.astamuse.asta4d.util.i18n;

import java.util.Locale;

import com.astamuse.asta4d.format.ParamOrderDependentFormatter;
import com.astamuse.asta4d.util.InvalidMessageException;

public class ResourceBundleHelper extends ResourceBundleHelperBase {

    public ResourceBundleHelper() {
        super();
    }

    public ResourceBundleHelper(Locale locale, ParamOrderDependentFormatter formatter) {
        super(locale, formatter);
    }

    public ResourceBundleHelper(Locale locale) {
        super(locale);
    }

    public ResourceBundleHelper(ParamOrderDependentFormatter formatter) {
        super(formatter);
    }

    public String getMessage(String key, Object... params) throws InvalidMessageException {
        return ResourceBundleUtil.getMessage((ParamOrderDependentFormatter) getFormatter(), getLocale(), key, params);
    }

}
