package com.astamuse.asta4d.web.dispatch.response.writer;

import javax.servlet.http.HttpServletResponse;

public interface ContentWriter<T> {

    public void writeResponse(HttpServletResponse response, T content) throws Exception;

}
