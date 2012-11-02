package com.astamuse.asta4d.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.astamuse.asta4d.extnode.ExtNodeConstants;

public class MessagesUtil {

    private static final List<String> EXCLUDE_ATTR_NAME_LIST = new ArrayList<>();

    private static final ResourceBundle.Control DEFAULT_LOCALE_EXCLUDE_CONTROL = new ResourceBundle.Control() {
        @Override
        public Locale getFallbackLocale(String paramString, Locale paramLocale) {
            // for not use default locale
            return null;
        }
    };

    static {
        EXCLUDE_ATTR_NAME_LIST.add(ExtNodeConstants.MSG_NODE_ATTR_KEY);
        EXCLUDE_ATTR_NAME_LIST.add(ExtNodeConstants.MSG_NODE_ATTR_LOCALE);
        EXCLUDE_ATTR_NAME_LIST.add(ExtNodeConstants.MSG_NODE_ATTR_EXTERNALIZE);
    }

    public static List<String> getExcludeAttrNameList() {
        return EXCLUDE_ATTR_NAME_LIST;
    }

    static List<Object> retrieveNumberedParamKeyList(Map<String, Object> paramMap) {
        List<Object> numberedParamNameList = new ArrayList<>();
        for (int index = 0; paramMap.containsKey(ExtNodeConstants.MSG_NODE_ATTR_PARAM_PREFIX + index); index++) {
            String key = ExtNodeConstants.MSG_NODE_ATTR_PARAM_PREFIX + index;
            Object value = paramMap.get(key);
            numberedParamNameList.add(value);
        }
        return numberedParamNameList;
    }

    static ResourceBundle.Control getDefaultLocaleExcludeControl() {
        return DEFAULT_LOCALE_EXCLUDE_CONTROL;
    }

    private MessagesUtil() {
    }
}
