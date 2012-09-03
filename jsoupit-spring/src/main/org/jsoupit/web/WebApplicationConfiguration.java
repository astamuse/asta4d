package org.jsoupit.web;

import org.jsoupit.template.Configuration;

public class WebApplicationConfiguration extends Configuration {

    public WebApplicationConfiguration() {
        this.setSnippetInvoker(new WebApplicationSnippetInvoker());
        this.setTemplateResolver(new WebApplicationTemplateResolver());
    }
}
