package com.astamuse.asta4d.web.form;

import org.apache.commons.lang3.StringUtils;

public class CascadeFormUtil {
    public static final String rewriteArrayIndexPlaceHolder(String s, int seq) {
        return StringUtils.replace(s, "@", String.valueOf(seq));
    }
}
