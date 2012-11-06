package com.astamuse.asta4d.web.dispatch.response.provider;

import java.util.Map;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.dispatch.annotation.ContentProvider;
import com.astamuse.asta4d.web.dispatch.response.RedirectActionWriter;

public class RedirectTargetProvider {

    public final static String FlashScopeDataContextKey = RedirectTargetProvider.class + "##FlashScopeDataContextKey";

    public final static class RedirectDescriptor {
        private String targetPath;
        private Map<String, Object> flashScopeData;

        public RedirectDescriptor(String targetPath, Map<String, Object> flashScopeData) {
            super();
            this.targetPath = targetPath;
            this.flashScopeData = flashScopeData;
        }

        public String getTargetPath() {
            return targetPath;
        }

        public Map<String, Object> getFlashScopeData() {
            return flashScopeData;
        }

    }

    private String targetPath;

    public RedirectTargetProvider(String targetPath) {
        this(targetPath, null);
    }

    public RedirectTargetProvider(String targetPath, Map<String, Object> flashScopeData) {
        if (flashScopeData != null) {
            Context context = Context.getCurrentThreadContext();
            context.setData(FlashScopeDataContextKey, flashScopeData);
        }
        this.targetPath = targetPath;
        /*
        String targetUrl;
        if (url.startsWith("/")) {
            WebApplicationContext context = (WebApplicationContext) Context.getCurrentThreadContext();
            targetUrl = context.getRequest().getContextPath() + url;
        } else {
            targetUrl = url;
        }
        Map<String, Object> data = flashScopeData == null ? Collections.<String, Object> emptyMap() : flashScopeData;
        this.descriptor = new RedirectDescriptor(targetUrl, data);
        */
    }

    @ContentProvider(writer = RedirectActionWriter.class)
    public RedirectDescriptor getRedirectDescriptor() throws Exception {
        Map<String, Object> flashScopeData = null;
        Context context = Context.getCurrentThreadContext();
        if (context != null) {
            flashScopeData = context.getData(FlashScopeDataContextKey);
        }

        return new RedirectDescriptor(targetPath, flashScopeData);
    }
}
