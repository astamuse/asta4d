package com.astamuse.asta4d.template.util;

public class SelectorUtil {

    public final static String not(String not) {
        return not(null, not);
    }

    public final static String not(String prefix, String not) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix == null ? "*" : prefix);
        sb.append(":not(").append(not).append(")");
        return sb.toString();
    }

    public final static String attr(String attr) {
        return attr(attr, null);
    }

    public final static String attr(String attr, String value) {
        return attr(null, attr, value);
    }

    public final static String attr(String prefix, String attr, String value) {
        StringBuilder sb = new StringBuilder();
        if (prefix != null) {
            sb.append(prefix);
        }
        sb.append("[").append(attr);
        if (value != null) {
            sb.append("=").append(value);
        }
        sb.append("]");
        return sb.toString();
    }

    public final static String id(String id) {
        return id(null, id);
    }

    public final static String id(String prefix, String id) {
        StringBuilder sb = new StringBuilder();
        if (prefix != null) {
            sb.append(prefix);
        }
        sb.append("#").append(id);
        return sb.toString();
    }
}
