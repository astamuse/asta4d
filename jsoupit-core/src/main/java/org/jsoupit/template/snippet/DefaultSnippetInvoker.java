package org.jsoupit.template.snippet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoupit.Configuration;
import org.jsoupit.Context;
import org.jsoupit.template.render.Renderer;
import org.jsoupit.template.snippet.extract.SnippetExtractor;
import org.jsoupit.template.snippet.interceptor.SnippetInterceptor;
import org.jsoupit.template.snippet.resolve.SnippetResolver;

public class DefaultSnippetInvoker implements SnippetInvoker {

    private final static ConcurrentHashMap<SnippetDeclarationInfo, Method> methodCache = new ConcurrentHashMap<>();

    private List<SnippetInterceptor> snippetInterceptorList = null;

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

        try {

            SnippetExecutionHolder execution = new SnippetExecutionHolder(info, instance, method, null, null);

            SnippetInterceptor lastInterceptor = beforeSnippet(execution);
            if (execution.getExecuteResult() == null) {
                Object[] params = execution.getParams();
                if (params == null) {
                    execution.setExecuteResult((Renderer) method.invoke(execution.getInstance()));
                } else {
                    execution.setExecuteResult((Renderer) method.invoke(execution.getInstance(), params));
                }

            }
            afterSnippet(lastInterceptor, info, execution);
            return execution.getExecuteResult();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new SnippetInvokeException(e);
        }
    }

    protected SnippetInterceptor beforeSnippet(SnippetExecutionHolder execution) throws SnippetInvokeException {
        SnippetInterceptor lastInterceptor = null;
        if (snippetInterceptorList == null)
            return lastInterceptor;
        for (SnippetInterceptor interceptor : snippetInterceptorList) {
            // TODO how about a exception in before snippet?
            interceptor.beforeSnippet(execution);
            lastInterceptor = interceptor;
            if (execution.getExecuteResult() != null) {
                break;
            }
        }
        return lastInterceptor;
    }

    protected void afterSnippet(SnippetInterceptor lastInterceptor, SnippetDeclarationInfo info, SnippetExecutionHolder execution)
            throws SnippetInvokeException {
        if (snippetInterceptorList == null)
            return;
        SnippetInterceptor interceptor = null;
        boolean foundStoppedPoint = false;
        for (int i = snippetInterceptorList.size() - 1; i >= 0; i--) {
            interceptor = snippetInterceptorList.get(i);
            if (!foundStoppedPoint) {
                foundStoppedPoint = interceptor == lastInterceptor;
            }
            if (foundStoppedPoint) {
                interceptor.afterSnippet(execution);
            }

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

    public List<SnippetInterceptor> getSnippetInterceptorList() {
        return snippetInterceptorList;
    }

    public void setSnippetInterceptorList(List<SnippetInterceptor> snippetInterceptorList) {
        this.snippetInterceptorList = snippetInterceptorList;
    }

}
