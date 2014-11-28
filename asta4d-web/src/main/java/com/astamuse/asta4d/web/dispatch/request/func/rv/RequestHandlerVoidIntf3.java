package com.astamuse.asta4d.web.dispatch.request.func.rv;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

@FunctionalInterface
public interface RequestHandlerVoidIntf3<T1, T2, T3> {
    @RequestHandler
    public void handle(T1 o1, T2 o2, T3 o3);
}
