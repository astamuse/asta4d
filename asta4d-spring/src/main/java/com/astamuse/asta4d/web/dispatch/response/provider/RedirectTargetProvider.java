package com.astamuse.asta4d.web.dispatch.response.provider;

import java.util.Map;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.dispatch.annotation.ContentProvider;
import com.astamuse.asta4d.web.dispatch.response.RedirectActionWriter;

public class RedirectTargetProvider {

    public final static String FlashScopeDataContextKey = RedirectTargetProvider.class + "##FlashScopeDataContextKey";

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
