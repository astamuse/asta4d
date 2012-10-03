package com.astamuse.asta4d.web.dispatch.mapping.ext.builtin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.web.dispatch.mapping.ext.RequestHandlerResolver;

public class DefaultHandlerResolver implements RequestHandlerResolver {

    private final static Logger logger = LoggerFactory.getLogger(DefaultHandlerResolver.class);

    @SuppressWarnings("rawtypes")
    @Override
    public Object resolve(Object declaration) {
        try {
            if (declaration instanceof Class) {
                return ((Class) declaration).newInstance();
            } else if (declaration instanceof String) {
                Class<?> clz = Class.forName(declaration.toString());
                return clz.newInstance();
            } else {
                return declaration;
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            logger.warn("Can not create request handler instance for:" + declaration.toString(), e);
            return null;
        }
    }

}
