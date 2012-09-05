package org.jsoupit.template.snippet.interceptor;

import org.jsoupit.template.snippet.SnippetExecutionHolder;
import org.jsoupit.template.snippet.SnippetInvokeException;

public interface SnippetInterceptor {

    public void afterSnippet(SnippetExecutionHolder execution) throws SnippetInvokeException;

    public void beforeSnippet(SnippetExecutionHolder execution) throws SnippetInvokeException;

}
