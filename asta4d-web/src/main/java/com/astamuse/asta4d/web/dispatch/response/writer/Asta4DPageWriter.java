package com.astamuse.asta4d.web.dispatch.response.writer;

import javax.servlet.http.HttpServletResponse;

import com.astamuse.asta4d.web.dispatch.response.provider.Asta4DPage;

public class Asta4DPageWriter implements ContentWriter<Asta4DPage> {

    @Override
    public void writeResponse(HttpServletResponse response, Asta4DPage content) throws Exception {
        Asta4DPage page = (Asta4DPage) content;
        response.setContentType(page.getContentType());
        page.output(response.getOutputStream());
    }

}
