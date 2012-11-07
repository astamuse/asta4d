package com.astamuse.asta4d.web.dispatch.response.provider;

import com.astamuse.asta4d.web.WebPage;
import com.astamuse.asta4d.web.dispatch.annotation.ContentProvider;

public class Asta4DPageProvider {

    private String path;

    public Asta4DPageProvider(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @ContentProvider
    public WebPage producePage() throws Exception {
        return new WebPage(path);
    }

}
