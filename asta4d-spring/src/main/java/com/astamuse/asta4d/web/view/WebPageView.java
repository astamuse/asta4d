package com.astamuse.asta4d.web.view;


public class WebPageView implements Asta4dView {

    private final String path;

    public WebPageView(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
