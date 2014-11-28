package com.astamuse.asta4d.web.dispatch.request.func.rv;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

@FunctionalInterface
public interface RequestHandlerVoidIntf1<T> {
    @RequestHandler
    public void handle(T o);
}
