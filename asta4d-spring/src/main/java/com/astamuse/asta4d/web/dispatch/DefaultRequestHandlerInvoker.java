package com.astamuse.asta4d.web.dispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.interceptor.base.Executor;
import com.astamuse.asta4d.interceptor.base.GenericInterceptor;
import com.astamuse.asta4d.interceptor.base.InterceptorUtil;
import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.web.dispatch.interceptor.ForwardDescriptorHolder;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerInterceptor;
import com.astamuse.asta4d.web.dispatch.mapping.DefaultForwardDescriptor;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.dispatch.request.RequestHandlerAdapter;
import com.astamuse.asta4d.web.dispatch.response.Asta4DPageProvider;
import com.astamuse.asta4d.web.dispatch.response.ContentProvider;
import com.astamuse.asta4d.web.dispatch.response.forward.ContentProviderForwardDescriptor;
import com.astamuse.asta4d.web.dispatch.response.forward.ForwardDescriptor;
import com.astamuse.asta4d.web.dispatch.response.forward.ForwardableException;

public class DefaultRequestHandlerInvoker implements RequestHandlerInvoker {

    /* (non-Javadoc)
     * @see com.astamuse.asta4d.web.dispatch.RequestHandlerInvoker#invoke(com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule)
     */
    @Override
    public ForwardDescriptor invoke(UrlMappingRule rule) throws Exception {
        RequestHandlerInvokeExecutor executor = new RequestHandlerInvokeExecutor(rule.getHandlerList());
        ForwardDescriptorHolder holder = new ForwardDescriptorHolder();
        InterceptorUtil.executeWithInterceptors(holder, buildInterceptorList(rule), executor);
        return holder.getForwardDescriptor();
    }

    /*
    private static WebPageView getForwardPageView(Map<Class<? extends ForwardDescriptor>, String> forwardDescriptors,
            ForwardDescriptor forwardDescriptor) {
        String path = forwardDescriptors.get(forwardDescriptor.getClass());
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        return new WebPageView(path);
    }
    */

    private List<RequestHandlerInterceptorWrapper> buildInterceptorList(UrlMappingRule rule) {
        List<RequestHandlerInterceptorWrapper> list = new ArrayList<>();
        for (RequestHandlerInterceptor interceptor : rule.getInterceptorList()) {
            list.add(new RequestHandlerInterceptorWrapper(rule, interceptor));
        }
        return list;
    }

    private static class RequestHandlerInterceptorWrapper implements GenericInterceptor<ForwardDescriptorHolder> {

        private final UrlMappingRule rule;
        private final RequestHandlerInterceptor interceptor;

        public RequestHandlerInterceptorWrapper(UrlMappingRule rule, RequestHandlerInterceptor interceptor) {
            this.rule = rule;
            this.interceptor = interceptor;
        }

        @Override
        public boolean beforeProcess(ForwardDescriptorHolder holder) throws Exception {
            interceptor.preHandle(rule, holder);
            return holder.getForwardDescriptor() != null;
        }

        @Override
        public void afterProcess(ForwardDescriptorHolder holder, ExceptionHandler exceptionHandler) {
            interceptor.postHandle(rule, holder, exceptionHandler);
        }
    }

    private static class RequestHandlerInvokeExecutor implements Executor<ForwardDescriptorHolder> {

        private final static Logger logger = LoggerFactory.getLogger(RequestHandlerInvokeExecutor.class);

        private final static DefaultForwardDescriptor defaultFd = new DefaultForwardDescriptor();

        private final List<Object> requestHandlerList;

        public RequestHandlerInvokeExecutor(List<Object> requestHandlerList) {
            this.requestHandlerList = requestHandlerList;
        }

        @Override
        public void execute(ForwardDescriptorHolder holder) throws Exception {
            ForwardDescriptor fd;
            for (Object handler : requestHandlerList) {

                if (handler instanceof RequestHandlerAdapter) {
                    fd = invokeHandler(((RequestHandlerAdapter) handler).asRequestHandler());
                } else {
                    fd = invokeHandler(handler);
                }
                if (fd != null) {
                    holder.setForwardDescriptor(fd);
                    break;
                }
            }
            holder.setForwardDescriptor(defaultFd);
        }

        private ForwardDescriptor invokeHandler(Object handler) throws InvocationTargetException, IllegalAccessException,
                DataOperationException, TemplateException {
            Method[] methodList = handler.getClass().getMethods();
            Method m = null;
            for (Method method : methodList) {
                if (method.isAnnotationPresent(RequestHandler.class)) {
                    m = method;
                    break;
                }
            }

            if (m == null) {
                // TODO maybe we can return a null?
                String msg = String.format("Request handler method not found:" + handler.getClass().getName());
                logger.error(msg);
                throw new InvocationTargetException(new RuntimeException(msg));
            }

            Object[] params = InjectUtil.getMethodInjectParams(m);
            if (params == null) {
                params = new Object[0];
            }
            Object result;
            try {
                result = m.invoke(handler, params);
                if (result == null) {
                    return null;
                } else if (result instanceof ForwardDescriptor) {
                    return (ForwardDescriptor) result;
                } else if (result instanceof String) {
                    return new ContentProviderForwardDescriptor(new Asta4DPageProvider(result.toString()));
                } else if (result instanceof ContentProvider) {
                    return new ContentProviderForwardDescriptor((ContentProvider) result);
                }
                throw new UnsupportedOperationException("Result Type:" + result.getClass().getName());
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof ForwardableException) {
                    throw (ForwardableException) e.getTargetException();
                }
                throw e;
            }

        }
    }

}
