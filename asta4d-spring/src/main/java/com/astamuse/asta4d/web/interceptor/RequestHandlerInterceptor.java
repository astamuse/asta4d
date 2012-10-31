package com.astamuse.asta4d.web.interceptor;

import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public interface RequestHandlerInterceptor {

    public void preHandle(UrlMappingRule rule, ViewHolder holder);

    public void postHandle(UrlMappingRule rule, ViewHolder holder, ExceptionHandler exceptionHandler);
}
