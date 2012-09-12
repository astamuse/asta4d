package com.astamuse.asta4d.snippet.interceptor;

import com.astamuse.asta4d.snippet.SnippetExecutionHolder;
import com.astamuse.asta4d.snippet.SnippetInvokeException;

public interface SnippetInterceptor {

    public void afterSnippet(SnippetExecutionHolder execution) throws SnippetInvokeException;

    public void beforeSnippet(SnippetExecutionHolder execution) throws SnippetInvokeException;

}
