package com.astamuse.asta4d.web;

import java.util.ArrayList;
import java.util.List;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.web.dispatch.RequestHandlerInvokerFactory;

public class WebApplicationConfiguration extends Configuration {

    private RequestHandlerInvokerFactory requestHandlerInvokerFactory = new RequestHandlerInvokerFactory();

    public WebApplicationConfiguration() {
        this.setTemplateResolver(new WebApplicationTemplateResolver());
        this.setContextDataFinder(new WebApplicationContextDataFinder());

        // we only allow request scope being reversely injected
        List<String> reverseInjectableScopes = new ArrayList<>();
        reverseInjectableScopes.add(WebApplicationContext.SCOPE_REQUEST);
        this.setReverseInjectableScopes(reverseInjectableScopes);
    }

    public RequestHandlerInvokerFactory getRequestHandlerInvokerFactory() {
        return requestHandlerInvokerFactory;
    }

    public void setRequestHandlerInvokerFactory(RequestHandlerInvokerFactory requestHandlerInvokerFactory) {
        this.requestHandlerInvokerFactory = requestHandlerInvokerFactory;
    }
}
