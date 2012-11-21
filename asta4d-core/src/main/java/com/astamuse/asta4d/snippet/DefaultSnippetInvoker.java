package com.astamuse.asta4d.snippet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.interceptor.base.Executor;
import com.astamuse.asta4d.interceptor.base.InterceptorUtil;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.snippet.extract.SnippetExtractor;
import com.astamuse.asta4d.snippet.interceptor.ContextDataAutowireInterceptor;
import com.astamuse.asta4d.snippet.interceptor.SnippetInitializeInterceptor;
import com.astamuse.asta4d.snippet.interceptor.SnippetInterceptor;
import com.astamuse.asta4d.snippet.resolve.SnippetResolver;

public class DefaultSnippetInvoker implements SnippetInvoker {

    private List<SnippetInterceptor> snippetInterceptorList = getDefaultSnippetInterceptorList();

    protected static class InterceptorResult {
        Renderer renderer = null;
        SnippetInterceptor finalInterceptor = null;
    }

    @Override
    public Renderer invoke(String renderDeclaration) throws SnippetNotResovlableException, SnippetInvokeException {
        Configuration conf = Context.getCurrentThreadContext().getConfiguration();

        SnippetExtractor extractor = conf.getSnippetExtractor();
        SnippetDeclarationInfo declaration = extractor.extract(renderDeclaration);

        SnippetResolver resolver = conf.getSnippetResolver();
        SnippetExcecutionInfo exeInfo = resolver.resloveSnippet(declaration);

        SnippetExecutionHolder execution = new SnippetExecutionHolder(declaration, exeInfo.getInstance(), exeInfo.getMethod(), null, null);
        Executor<SnippetExecutionHolder> executor = new Executor<SnippetExecutionHolder>() {
            @Override
            public void execute(SnippetExecutionHolder executionHolder) throws Exception {
                Object instance = executionHolder.getInstance();
                Method method = executionHolder.getMethod();
                Object[] params = executionHolder.getParams();
                if (params == null) {
                    executionHolder.setExecuteResult((Renderer) method.invoke(instance));
                } else {
                    executionHolder.setExecuteResult((Renderer) method.invoke(instance, params));
                }
            }
        };
        try {
            InterceptorUtil.executeWithInterceptors(execution, snippetInterceptorList, executor);
            return execution.getExecuteResult();
        } catch (SnippetInvokeException ex) {
            throw ex;
        } catch (Exception ex) {
            Object[] params = execution.getParams();

            String msg = "execute with params:" + (params == null ? null : Arrays.asList(params));
            throw new SnippetInvokeException(declaration, msg, ex);
        }

    }

    protected List<SnippetInterceptor> getDefaultSnippetInterceptorList() {
        List<SnippetInterceptor> list = new ArrayList<>();
        list.add(new ContextDataAutowireInterceptor());
        list.add(new SnippetInitializeInterceptor());
        return list;
    }

    public List<SnippetInterceptor> getSnippetInterceptorList() {
        return snippetInterceptorList;
    }

    public void setSnippetInterceptorList(List<SnippetInterceptor> snippetInterceptorList) {
        this.snippetInterceptorList.clear();
        this.snippetInterceptorList.addAll(getDefaultSnippetInterceptorList());
        if (snippetInterceptorList != null) {
            this.snippetInterceptorList.addAll(snippetInterceptorList);
        }
    }

}
