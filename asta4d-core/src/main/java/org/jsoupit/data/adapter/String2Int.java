package org.jsoupit.data.adapter;

import org.jsoupit.data.ContextDataAdapter;

public class String2Int implements ContextDataAdapter {

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
