package com.astamuse.asta4d.web.dispatch.request.func.rv;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

@FunctionalInterface
public interface RequestHandlerVoidIntf {
    @RequestHandler
    public void handle();
}
