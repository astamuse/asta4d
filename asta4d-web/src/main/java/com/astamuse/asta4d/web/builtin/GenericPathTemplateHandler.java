package com.astamuse.asta4d.web.builtin;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

public class GenericPathTemplateHandler extends AbstractGenericPathHandler {

    public GenericPathTemplateHandler() {
    }

    public GenericPathTemplateHandler(String basePath) {
        super(basePath);
    }

    @RequestHandler
    public String handle(UrlMappingRule currentRule) {
        return super.convertPath(currentRule);
    }
}
