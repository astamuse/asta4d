package com.astamuse.asta4d.util.i18n;

import com.astamuse.asta4d.Configuration;

/**
 * This class is used for retrieving and using configured helper more smoothly. If there is a customized helper implementation, a new
 * assistant class can be created too just like this one.
 * 
 * @author e-ryu
 * 
 */
public class I18nMessageHelperTypeAssistant {
    public static enum Type {
        Mapped, Ordered
    }

    /**
     * we do not declare it as final because we will recreate the instance for test purpose
     */
    private static I18nMessageHelperTypeAssistant _instance = new I18nMessageHelperTypeAssistant();

    /**
     * DO NOT USE IT!!! IT IS FOR TEST PURPOSE!!!
     */
    @Deprecated
    public static void reCreateInternalInstance() {
        if (System.getProperty("I18nMessageHelperTypeAssistant.Test") != null) {
            _instance = new I18nMessageHelperTypeAssistant();
        } else {
            throw new UnsupportedOperationException("reCreateInternalInstance() is used for test purpose. DO NOT USE IT!!!");
        }
    }

    private final Type configuredHelperType;
    private final MappedValueI18nMessageHelper mappedTypeHelper;
    private final OrderedValueI18nMessageHelper orderedTypeHelper;

    private I18nMessageHelperTypeAssistant() {
        I18nMessageHelper helper = Configuration.getConfiguration().getI18nMessageHelper();
        if (helper instanceof MappedValueI18nMessageHelper) {
            configuredHelperType = Type.Mapped;
            mappedTypeHelper = (MappedValueI18nMessageHelper) helper;
            orderedTypeHelper = null;
        } else if (helper instanceof OrderedValueI18nMessageHelper) {
            configuredHelperType = Type.Ordered;
            mappedTypeHelper = null;
            orderedTypeHelper = (OrderedValueI18nMessageHelper) helper;
        } else {
            configuredHelperType = null;
            mappedTypeHelper = null;
            orderedTypeHelper = null;
        }

    }

    public static final Type configuredHelperType() {
        return _instance.configuredHelperType;
    }

    public static final MappedValueI18nMessageHelper getConfiguredMappedHelper() {
        return _instance.mappedTypeHelper;
    }

    public static final OrderedValueI18nMessageHelper getConfiguredOrderedHelper() {
        return _instance.orderedTypeHelper;
    }
}
