package com.astamuse.asta4d.web.form;

import java.util.regex.Pattern;

public class CascadeArrayFunctionsHelper {
    static final Pattern[] PlaceHolderSearchPattern = new Pattern[100];
    static {
        for (int i = 0; i < PlaceHolderSearchPattern.length; i++) {
            PlaceHolderSearchPattern[i] = Pattern.compile("(^|.*[^@])(@{" + (i + 1) + "})([^@].*|$)");
        }
    }
}
