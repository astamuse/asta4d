package com.astamuse.asta4d.web.dispatch.response;

import javax.servlet.http.HttpServletResponse;

public interface ContentWriter {

    public void writeResponse(HttpServletResponse response, Object content) throws Exception;

}
