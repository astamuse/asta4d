package com.astamuse.asta4d.web.dispatch;

public interface RedirectInterceptor {

    public void beforeRedirect();

    public void afterRedirectDataRestore();
}
