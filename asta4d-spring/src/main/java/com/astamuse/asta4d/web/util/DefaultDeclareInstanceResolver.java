package com.astamuse.asta4d.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDeclareInstanceResolver implements DeclareInstanceResolver {

    private final static Logger logger = LoggerFactory.getLogger(DefaultDeclareInstanceResolver.class);

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
            logger.warn("Can not create instance for:" + declaration.toString(), e);
            return null;
        }
    }

}
