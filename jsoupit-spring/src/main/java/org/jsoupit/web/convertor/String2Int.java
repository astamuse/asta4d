package org.jsoupit.web.convertor;

import org.jsoupit.template.snippet.interceptor.ContextDataConvertor;

public class String2Int implements ContextDataConvertor {

    @Override
    public Class<?> getSourceType() {
        return String.class;
    }

    @Override
    public Class<?> getTargetType() {
        return Integer.class;
    }

    @Override
    public Object convert(Object obj) {
        return Integer.parseInt(obj.toString());
    }

}
