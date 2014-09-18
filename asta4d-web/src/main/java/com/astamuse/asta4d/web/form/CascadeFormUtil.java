package com.astamuse.asta4d.web.form;

import org.apache.commons.lang3.StringUtils;

public class CascadeFormUtil {
    public static final String rewriteFieldName(String name, int seq) {
        return StringUtils.replace(name, "@", String.valueOf(seq));
    }
}
