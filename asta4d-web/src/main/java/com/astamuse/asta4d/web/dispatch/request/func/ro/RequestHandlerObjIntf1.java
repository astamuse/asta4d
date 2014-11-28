package com.astamuse.asta4d.web.dispatch.request.func.ro;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

@FunctionalInterface
public interface RequestHandlerObjIntf1<T> {
    @RequestHandler
    public Object handle(T o);
}
