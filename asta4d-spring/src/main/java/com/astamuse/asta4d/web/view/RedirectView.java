package com.astamuse.asta4d.web.view;

import java.util.Collections;
import java.util.Map;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationContext;

public class RedirectView implements Asta4dView {

    private final String url;

    private final Map<String, Object> flashScopeData;

    public RedirectView(String url) {
        this(url, Collections.<String, Object> emptyMap());
    }

    public RedirectView(String url, Map<String, Object> flashScopeData) {
        if (url.startsWith("/")) {
            WebApplicationContext context = (WebApplicationContext) Context.getCurrentThreadContext();
            this.url = context.getRequest().getContextPath() + url;
        } else {
            this.url = url;
        }
        this.flashScopeData = flashScopeData;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, Object> getFlashScopeData() {
        return Collections.unmodifiableMap(flashScopeData);
    }
}