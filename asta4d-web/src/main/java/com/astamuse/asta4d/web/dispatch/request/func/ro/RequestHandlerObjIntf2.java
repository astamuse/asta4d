package com.astamuse.asta4d.web.dispatch.request.func.ro;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

@FunctionalInterface
public interface RequestHandlerObjIntf2<T1, T2> {
    @RequestHandler
    public Object handle(T1 o1, T2 o2);
}
