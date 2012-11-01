package com.astamuse.asta4d.web.dispatch.response;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.util.RedirectUtil;

public class RedirectActionProvider implements ContentProvider {

    private final String url;

    private final Map<String, Object> flashScopeData;

    public RedirectActionProvider(String url) {
        this(url, Collections.<String, Object> emptyMap());
    }

    public RedirectActionProvider(String url, Map<String, Object> flashScopeData) {
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

    @Override
    public void produce(HttpServletResponse response) throws Exception {
        String redirectUrl = RedirectUtil.setFlashScopeData(url, flashScopeData);
        response.sendRedirect(redirectUrl);
    }

}
