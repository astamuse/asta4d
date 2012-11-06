package com.astamuse.asta4d.web.dispatch.response;

import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.web.dispatch.response.provider.RestResult;

public class RestResultWriter implements ContentWriter {

    @Override
    public void writeResponse(HttpServletResponse response, Object content) throws Exception {
        if (content == null) {
            response.setStatus(HttpURLConnection.HTTP_OK);
        } else {
            response.setStatus(((RestResult) content).getStatus());
        }
    }

}
