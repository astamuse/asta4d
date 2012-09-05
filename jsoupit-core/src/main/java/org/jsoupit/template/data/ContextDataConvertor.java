package org.jsoupit.template.data;

public interface ContextDataConvertor {

    public Class<?> getSourceType();

    public Class<?> getTargetType();

    public Object convert(Object obj);

}
