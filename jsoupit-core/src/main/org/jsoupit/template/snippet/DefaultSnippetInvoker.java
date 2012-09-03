package org.jsoupit.template.snippet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoupit.template.Configuration;
import org.jsoupit.template.Context;
import org.jsoupit.template.render.Renderer;
import org.jsoupit.template.snippet.extract.SnippetExtractor;
import org.jsoupit.template.snippet.interceptor.SnippetInterceptor;
import org.jsoupit.template.snippet.resolve.SnippetResolver;

public class DefaultSnippetInvoker implements SnippetInvoker {

    private final static ConcurrentHashMap<SnippetInfo, Method> methodCache = new ConcurrentHashMap<>();

    private List<SnippetInterceptor> snippetInterceptorList = null;

    protected static class InterceptorResult {
        Renderer renderer = null;
        SnippetInterceptor finalInterceptor = null;
    }

    @Override
    public Renderer invoke(String renderDeclaration) throws SnippetNotResovlableException, SnippetInvokeException {
        Configuration conf = Context.getCurrentThreadContext().getConfiguration();

        SnippetExtractor extractor = conf.getSnippetExtractor();
        SnippetInfo info = extractor.extract(renderDeclaration);

        SnippetResolver resolver = conf.getSnippetResolver();
        Object instance = resolver.findSnippet(info.getSnippetName());

        Method method = getSnippetMethod(info, instance);

        try {
            InterceptorResult iresult = beforeSnippet(info, instance, method);
            if (iresult.renderer == null) {
                iresult.renderer = (Renderer) method.invoke(instance);
            }
            afterSnippet(iresult, info, instance, method);
            return iresult.renderer;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new SnippetNotResovlableException(e);
        }
    }

    protected InterceptorResult beforeSnippet(SnippetInfo info, Object instance, Method method) throws SnippetInvokeException {
        InterceptorResult iresult = new InterceptorResult();
        if (snippetInterceptorList == null)
            return iresult;
        for (SnippetInterceptor interceptor : snippetInterceptorList) {
            iresult.renderer = interceptor.beforeSnippet(info, instance, method);
            iresult.finalInterceptor = interceptor;
            if (iresult.renderer != null)
                break;
        }
        return iresult;
    }

    protected void afterSnippet(InterceptorResult iresult, SnippetInfo info, Object instance, Method method) throws SnippetInvokeException {
        if (snippetInterceptorList == null)
            return;
        SnippetInterceptor interceptor = null;
        boolean foundStoppedPoint = false;
        for (int i = snippetInterceptorList.size() - 1; i >= 0; i--) {
            interceptor = snippetInterceptorList.get(i);
            if (!foundStoppedPoint) {
                foundStoppedPoint = interceptor == iresult.finalInterceptor;
            }
            if (foundStoppedPoint) {
                interceptor.afterSnippet(info, instance, method);
            }

        }
    }

    protected Method getSnippetMethod(SnippetInfo info, Object snippetInstance) throws SnippetNotResovlableException {
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
            if (method.getName().equals(methodName) &&
                    method.getParameterTypes().length == 0 &&
                    rendererCls.isAssignableFrom(method.getReturnType())) {
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
