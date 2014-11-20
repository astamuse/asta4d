package com.astamuse.asta4d.web.builtin;

import com.astamuse.asta4d.template.TemplateNotFoundException;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

public class GenericPathTemplateHandler extends AbstractGenericPathHandler {

    public GenericPathTemplateHandler() {
    }

    public GenericPathTemplateHandler(String basePath) {
        super(basePath);
    }

    @RequestHandler
    public Object handle(UrlMappingRule currentRule) {
        String path = super.convertPath(currentRule);
        if (path == null) {
            WebApplicationContext context = WebApplicationContext.getCurrentThreadWebApplicationContext();
            String url = context.getAccessURI();
            return new TemplateNotFoundException("Generically convert from path:" + url);
        } else {
            return path;
        }
    }
}
