package com.astamuse.asta4d.web.dispatch.response.provider;

import com.astamuse.asta4d.web.WebPage;
import com.astamuse.asta4d.web.dispatch.annotation.ContentProvider;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public class Asta4DPageProvider {

    public final static String AttrBodyOnly = Asta4DPageProvider.class.getName() + "##bodyOnly";

    private String path;

    public Asta4DPageProvider(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @ContentProvider
    public WebPage producePage(UrlMappingRule rule) throws Exception {
        return new WebPage(path, rule.hasAttribute(AttrBodyOnly));
    }

}
