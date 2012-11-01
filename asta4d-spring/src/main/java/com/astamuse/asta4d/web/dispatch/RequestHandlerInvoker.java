package com.astamuse.asta4d.web.dispatch;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.response.forward.ForwardDescriptor;

public interface RequestHandlerInvoker {

    public ForwardDescriptor invoke(UrlMappingRule rule) throws Exception;

}