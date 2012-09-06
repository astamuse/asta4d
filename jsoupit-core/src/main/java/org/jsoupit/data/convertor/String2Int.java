package org.jsoupit.data.convertor;

import org.jsoupit.data.ContextDataConvertor;

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
