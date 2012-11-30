package com.astamuse.asta4d.web.dispatch.response.writer;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.Pair;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

@SuppressWarnings("rawtypes")
public class SerialWriter implements ContentWriter<List<Pair<Object, ContentWriter>>> {

    @SuppressWarnings("unchecked")
    @Override
    public void writeResponse(UrlMappingRule currentRule, HttpServletResponse response, List<Pair<Object, ContentWriter>> content)
            throws Exception {
        for (Pair<Object, ContentWriter> pair : content) {
            pair.getRight().writeResponse(currentRule, response, pair.getLeft());
        }
    }
}
