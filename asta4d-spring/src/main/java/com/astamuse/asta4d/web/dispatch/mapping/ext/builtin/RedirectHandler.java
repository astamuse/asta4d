package com.astamuse.asta4d.web.dispatch.mapping.ext.builtin;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.dispatch.RequestDispatcher;
import com.astamuse.asta4d.web.dispatch.annotation.RequestHandler;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.view.RedirectView;

public class RedirectHandler {

    @RequestHandler
    public RedirectView go() {
        Context context = Context.getCurrentThreadContext();
        UrlMappingRule rule = context.getData(RequestDispatcher.KEY_CURRENT_RULE);
        return new RedirectView(rule.getDefaultTargetPath());
    }

}
