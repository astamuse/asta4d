package org.jsoupit.web;

import java.io.InputStream;

import javax.servlet.ServletContext;

import org.jsoupit.template.TemplateResolver;

public class WebApplicationTemplateResolver extends TemplateResolver {

    private ServletContext servletContext;

    public WebApplicationTemplateResolver() {
        super();
    }

    public WebApplicationTemplateResolver(ServletContext servletContext) {
        super();
        this.servletContext = servletContext;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    protected InputStream loadResource(String path) {
        // TODO test whether it can work well in an unpacked war deployment
        return servletContext.getResourceAsStream(path);
    }

}
