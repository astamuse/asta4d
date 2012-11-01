package com.astamuse.asta4d.web.dispatch.response;

import javax.servlet.http.HttpServletResponse;

public interface ContentProvider {

    public void produce(HttpServletResponse response) throws Exception;

}
