package com.astamuse.asta4d.web.util;

import java.util.List;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationConfiguration;

public class DeclareInstanceUtil {

    private final static DeclareInstanceResolver defaultResolver = new DefaultDeclareInstanceResolver();

    @SuppressWarnings("unchecked")
    public final static <T> T createInstance(Object declaration) {
        WebApplicationConfiguration conf = (WebApplicationConfiguration) Context.getCurrentThreadContext().getConfiguration();
        List<DeclareInstanceResolver> resolverList = conf.getInstanceResolverList();
        Object handler = null;
        for (DeclareInstanceResolver resolver : resolverList) {
            handler = resolver.resolve(declaration);
            if (handler != null) {
                break;
            }
        }
        if (handler == null) {
            handler = defaultResolver.resolve(declaration);
        }
        return (T) handler;
    }
}
