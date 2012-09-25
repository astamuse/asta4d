package com.astamuse.asta4d.web.view;

import java.util.Locale;

public class WebPageView implements Asta4dView {

    private final String path;

    private final Locale locale;

    public WebPageView(String path, Locale locale) {
        this.path = path;
        this.locale = locale;
    }

    public String getPath() {
        return path;
    }

    public Locale getLocale() {
        return locale;
    }
}
