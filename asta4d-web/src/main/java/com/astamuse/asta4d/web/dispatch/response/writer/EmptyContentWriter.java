package com.astamuse.asta4d.web.dispatch.response.writer;

import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public class EmptyContentWriter implements ContentWriter<Object> {

    @Override
    public void writeResponse(UrlMappingRule currentRule, HttpServletResponse response, Object content) throws Exception {
        // do nothing
    }

}
