package com.astamuse.asta4d.web.dispatch.response;

import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.web.util.JsonUtil;

public class JsonWriter implements ContentWriter {

    @Override
    public void writeResponse(HttpServletResponse response, Object content) throws Exception {
        JsonUtil.toJson(response.getOutputStream(), content);
    }

}
