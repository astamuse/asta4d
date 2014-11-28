package com.astamuse.asta4d.web.dispatch.request.func.rv;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

@FunctionalInterface
public interface RequestHandlerVoidIntf2<T1, T2> {
    @RequestHandler
    public void handle(T1 o1, T2 o2);
}
