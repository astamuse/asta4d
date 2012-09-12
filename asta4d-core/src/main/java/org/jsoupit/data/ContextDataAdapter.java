package org.jsoupit.data;

public interface ContextDataAdapter {

    public Class<?> getSourceType();

    public Class<?> getTargetType();

    public Object convert(Object obj);

}
