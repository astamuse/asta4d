package com.astamuse.asta4d.web.dispatch.response.writer;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.Pair;

@SuppressWarnings("rawtypes")
public class SerialWriter implements ContentWriter<List<Pair<Object, ContentWriter>>> {

    @SuppressWarnings("unchecked")
    @Override
    public void writeResponse(HttpServletResponse response, List<Pair<Object, ContentWriter>> content) throws Exception {
        for (Pair<Object, ContentWriter> pair : content) {
            pair.getRight().writeResponse(response, pair.getLeft());
        }
    }
}
