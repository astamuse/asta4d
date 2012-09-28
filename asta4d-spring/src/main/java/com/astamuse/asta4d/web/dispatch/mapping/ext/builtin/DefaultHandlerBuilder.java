package com.astamuse.asta4d.web.dispatch.mapping.ext.builtin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.web.dispatch.mapping.ext.RequestHandlerBuilder;

public class DefaultHandlerBuilder implements RequestHandlerBuilder {

    private final static Logger logger = LoggerFactory.getLogger(DefaultHandlerBuilder.class);

    @SuppressWarnings("rawtypes")
    @Override
    public Object createRequestHandler(Object declaration) {
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
