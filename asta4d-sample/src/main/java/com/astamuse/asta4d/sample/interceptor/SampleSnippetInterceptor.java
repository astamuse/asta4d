package com.astamuse.asta4d.sample.interceptor;

import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.snippet.SnippetExecutionHolder;
import com.astamuse.asta4d.snippet.interceptor.SnippetInterceptor;

public class SampleSnippetInterceptor implements SnippetInterceptor {

    @Override
    public boolean beforeProcess(SnippetExecutionHolder executionHolder) throws Exception {
        System.out.println("[SampleSnippetInterceptor:beforeProcess]" + getSnippetNameMethod(executionHolder));
        return false;
    }

    public void afterProcess(SnippetExecutionHolder executionHolder, ExceptionHandler exceptionHandler) {
        System.out.println("[SampleSnippetInterceptor:afterProcess]" + getSnippetNameMethod(executionHolder));
    }

    private String getSnippetNameMethod(SnippetExecutionHolder executionHolder) {
        return executionHolder.getDeclarationInfo().getSnippetName() + ":" + executionHolder.getMethod().getName();
    }
}
