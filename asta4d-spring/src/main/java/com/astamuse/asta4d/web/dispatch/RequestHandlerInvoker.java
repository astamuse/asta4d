package com.astamuse.asta4d.web.dispatch;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public interface RequestHandlerInvoker {

    public Object invoke(UrlMappingRule rule) throws Exception;

}