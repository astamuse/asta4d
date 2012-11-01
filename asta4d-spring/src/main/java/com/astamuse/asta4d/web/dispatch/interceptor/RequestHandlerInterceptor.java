package com.astamuse.asta4d.web.dispatch.interceptor;

import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public interface RequestHandlerInterceptor {

    public void preHandle(UrlMappingRule rule, ForwardDescriptorHolder holder);

    public void postHandle(UrlMappingRule rule, ForwardDescriptorHolder holder, ExceptionHandler exceptionHandler);
}
