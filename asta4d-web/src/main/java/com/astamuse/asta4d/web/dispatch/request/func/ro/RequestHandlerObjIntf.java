package com.astamuse.asta4d.web.dispatch.request.func.ro;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

@FunctionalInterface
public interface RequestHandlerObjIntf {
    @RequestHandler
    public Object handle();
}
