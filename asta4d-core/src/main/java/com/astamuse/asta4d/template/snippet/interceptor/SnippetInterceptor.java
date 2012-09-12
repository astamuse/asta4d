package com.astamuse.asta4d.template.snippet.interceptor;

import com.astamuse.asta4d.template.snippet.SnippetExecutionHolder;
import com.astamuse.asta4d.template.snippet.SnippetInvokeException;

public interface SnippetInterceptor {

    public void afterSnippet(SnippetExecutionHolder execution) throws SnippetInvokeException;

    public void beforeSnippet(SnippetExecutionHolder execution) throws SnippetInvokeException;

}
