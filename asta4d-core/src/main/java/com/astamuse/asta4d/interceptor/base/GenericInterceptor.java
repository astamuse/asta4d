package com.astamuse.asta4d.interceptor.base;

public interface GenericInterceptor<H> {

    public boolean beforeProcess(H executionHolder) throws Exception;

    public void afterProcess(H executionHolder, ExceptionHandler exceptionHandler);
}
