package com.astamuse.asta4d.web.dispatch.response.provider;

import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public class EmptyContentProvider implements ContentProvider {

    @Override
    public boolean isContinuable() {
        return false;
    }

    @Override
    public void produce(UrlMappingRule currentRule, HttpServletResponse response) throws Exception {
        // do nothing
    }

}
