package com.astamuse.asta4d.snippet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.interceptor.Executor;
import com.astamuse.asta4d.interceptor.InterceptorUtil;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.snippet.extract.SnippetExtractor;
import com.astamuse.asta4d.snippet.interceptor.ContextDataAutowireInterceptor;
import com.astamuse.asta4d.snippet.interceptor.SnippetInterceptor;
import com.astamuse.asta4d.snippet.resolve.SnippetResolver;

public class DefaultSnippetInvoker implements SnippetInvoker {

    private final static ConcurrentHashMap<SnippetDeclarationInfo, Method> methodCache = new ConcurrentHashMap<>();

    private List<SnippetInterceptor> snippetInterceptorList = getDefaultSnippetInterceptorList();

    protected static class InterceptorResult {
        Renderer renderer = null;
        SnippetInterceptor finalInterceptor = null;
    }

    @Override
    public Renderer invoke(String renderDeclaration) throws SnippetNotResovlableException, SnippetInvokeException {
        Configuration conf = Context.getCurrentThreadContext().getConfiguration();

        SnippetExtractor extractor = conf.getSnippetExtractor();
        SnippetDeclarationInfo info = extractor.extract(renderDeclaration);

        SnippetResolver resolver = conf.getSnippetResolver();
        Object instance = resolver.findSnippet(info.getSnippetName());

        Method method = getSnippetMethod(info, instance);

        SnippetExecutionHolder execution = new SnippetExecutionHolder(info, instance, method, null, null);
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
            throw new SnippetInvokeException(ex);
        }

    }

    protected Method getSnippetMethod(SnippetDeclarationInfo info, Object snippetInstance) throws SnippetNotResovlableException {
        Method m = methodCache.get(info);
        if (m == null) {
            m = findSnippetMethod(snippetInstance, info.getSnippetHandler());
            if (m == null) {
                throw new SnippetNotResovlableException("Snippet cannot be resolved for " + info);
            }
            // we do not mind that the exited method instance would be overrode
            methodCache.put(info, m);
        }
        return m;
    }

    protected Method findSnippetMethod(Object snippetInstance, String methodName) {
        Method[] methodList = snippetInstance.getClass().getMethods();
        Class<?> rendererCls = Renderer.class;
        for (Method method : methodList) {
            if (method.getName().equals(methodName) && rendererCls.isAssignableFrom(method.getReturnType())) {
                return method;
            }
        }
        return null;
    }

    protected List<SnippetInterceptor> getDefaultSnippetInterceptorList() {
        List<SnippetInterceptor> list = new ArrayList<>();
        list.add(new ContextDataAutowireInterceptor());
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
