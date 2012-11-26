package com.astamuse.asta4d.web.dispatch.response;

import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.web.WebPage;

public class Asta4DPageWriter implements ContentWriter {

    @Override
    public void writeResponse(HttpServletResponse response, Object content) throws Exception {
        WebPage page = (WebPage) content;
        response.setContentType(page.getContentType());
        page.output(response.getOutputStream());
    }

}
