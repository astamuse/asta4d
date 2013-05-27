package com.astamuse.asta4d.web.builtin;

import javax.servlet.http.HttpServletRequest;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

public class GenericPathTemplateHandler extends AbstractGenericPathHandler {

    public GenericPathTemplateHandler() {
    }

    @RequestHandler
    public String handle(HttpServletRequest request, UrlMappingRule currentRule) {
        return super.convertPath(request, currentRule);
    }
}
