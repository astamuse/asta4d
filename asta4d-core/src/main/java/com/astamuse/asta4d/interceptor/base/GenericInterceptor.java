package com.astamuse.asta4d.interceptor.base;

//TODO add a mechanism to allow resolve exception in after process rather than throw it
public interface GenericInterceptor<H> {

    public boolean beforeProcess(H executionHolder) throws Exception;

    public void afterProcess(H executionHolder, ExceptionHandler exceptionHandler);
}
