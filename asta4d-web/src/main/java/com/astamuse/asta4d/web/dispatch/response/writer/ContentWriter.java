package com.astamuse.asta4d.web.dispatch.response.writer;

import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public interface ContentWriter<T> {

    public void writeResponse(UrlMappingRule currentRule, HttpServletResponse response, T content) throws Exception;

}
