package org.jsoupit.template.snippet.interceptor;

import java.lang.reflect.Method;

import org.jsoupit.template.render.Renderer;
import org.jsoupit.template.snippet.SnippetInfo;
import org.jsoupit.template.snippet.SnippetInvokeException;

public interface SnippetInterceptor {

    public void afterSnippet(SnippetInfo snippetInfo, Object instance, Method method) throws SnippetInvokeException;

    public Renderer beforeSnippet(SnippetInfo snippetInfo, Object instance, Method method) throws SnippetInvokeException;

}
