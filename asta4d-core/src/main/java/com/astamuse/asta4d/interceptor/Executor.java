package com.astamuse.asta4d.interceptor;

public interface Executor<H> {

    public void execute(H executionHolder) throws Exception;

}
