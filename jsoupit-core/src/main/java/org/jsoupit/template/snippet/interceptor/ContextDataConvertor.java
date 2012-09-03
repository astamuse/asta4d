package org.jsoupit.template.snippet.interceptor;

public interface ContextDataConvertor {

    public Class<?> getSourceType();

    public Class<?> getTargetType();

    public Object convert(Object obj);

}
