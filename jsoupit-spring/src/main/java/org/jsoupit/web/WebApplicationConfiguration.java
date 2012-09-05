package org.jsoupit.web;

import java.util.ArrayList;
import java.util.List;

import org.jsoupit.template.Configuration;

public class WebApplicationConfiguration extends Configuration {

    public WebApplicationConfiguration() {
        this.setSnippetInvoker(new WebApplicationSnippetInvoker());
        this.setTemplateResolver(new WebApplicationTemplateResolver());
        this.setContextDataFinder(new WebApplicationContextDataFinder());

        // we only allow request scope being reversely injected
        List<String> reverseInjectableScopes = new ArrayList<>();
        reverseInjectableScopes.add(WebApplicationContext.SCOPE_REQUEST);
        this.setReverseInjectableScopes(reverseInjectableScopes);
    }
}
