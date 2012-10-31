package com.astamuse.asta4d.web.dispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.interceptor.base.Executor;
import com.astamuse.asta4d.interceptor.base.GenericInterceptor;
import com.astamuse.asta4d.interceptor.base.InterceptorUtil;
import com.astamuse.asta4d.web.dispatch.annotation.RequestHandler;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.forward.ForwardDescriptor;
import com.astamuse.asta4d.web.forward.ForwardableException;
import com.astamuse.asta4d.web.interceptor.RequestHandlerInterceptor;
import com.astamuse.asta4d.web.interceptor.ViewHolder;
import com.astamuse.asta4d.web.view.Asta4dView;
import com.astamuse.asta4d.web.view.RedirectView;
import com.astamuse.asta4d.web.view.WebPageView;

public class RequestHandlerInvoker {

    private List<RequestHandlerInterceptor> interceptorList = Collections.emptyList();

    Asta4dView invoke(UrlMappingRule rule) throws Exception {
        RequestHandlerInvokeExecutor executor = new RequestHandlerInvokeExecutor(rule.getForwardDescriptors());
        ViewAndHandlersHolder holder = new ViewAndHandlersHolder(rule.getHandlerList());
        try {
            InterceptorUtil.executeWithInterceptors(holder, buildList(rule), executor);
            return holder.getViewHolder().getView();
        } catch (ForwardableException e) {
            WebPageView view = getForwardPageView(rule.getForwardDescriptors(), e.getForwardDescriptor());
            if (view != null) {
                return view;
            }
            throw e.getCause();
        }
    }

    void setInterceptorList(List<RequestHandlerInterceptor> interceptorList) {
        this.interceptorList = interceptorList;
    }

    private static WebPageView getForwardPageView(Map<Class<? extends ForwardDescriptor>, String> forwardDescriptors,
            ForwardDescriptor forwardDescriptor) {
        String path = forwardDescriptors.get(forwardDescriptor.getClass());
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        return new WebPageView(path);
    }

    private List<RequestHandlerInterceptorWrapper> buildList(UrlMappingRule rule) {
        List<RequestHandlerInterceptorWrapper> list = new ArrayList<>();
        for (RequestHandlerInterceptor interceptor : interceptorList) {
            list.add(new RequestHandlerInterceptorWrapper(rule, interceptor));
        }
        return list;
    }

    private static class RequestHandlerInterceptorWrapper implements GenericInterceptor<ViewAndHandlersHolder> {

        private final UrlMappingRule rule;
        private final RequestHandlerInterceptor interceptor;

        public RequestHandlerInterceptorWrapper(UrlMappingRule rule, RequestHandlerInterceptor interceptor) {
            this.rule = rule;
            this.interceptor = interceptor;
        }

        @Override
        public boolean beforeProcess(ViewAndHandlersHolder holder) throws Exception {
            interceptor.preHandle(rule, holder.getViewHolder());
            return true;
        }

        @Override
        public void afterProcess(ViewAndHandlersHolder holder, ExceptionHandler exceptionHandler) {
            interceptor.postHandle(rule, holder.getViewHolder(), exceptionHandler);
        }
    }

    private static class RequestHandlerInvokeExecutor implements Executor<ViewAndHandlersHolder> {

        private final Logger logger = LoggerFactory.getLogger(RequestHandlerInvokeExecutor.class);

        private final Map<Class<? extends ForwardDescriptor>, String> forwardDescriptors;

        public RequestHandlerInvokeExecutor(Map<Class<? extends ForwardDescriptor>, String> forwardDescriptors) {
            this.forwardDescriptors = forwardDescriptors;
        }

        @Override
        public void execute(ViewAndHandlersHolder holder) throws Exception {
            ViewHolder viewHolder = holder.getViewHolder();
            List<Object> handlers = holder.getHandlers();
            for (Object handler : handlers) {
                Asta4dView view = null;
                if (handler instanceof RequestHandlerAdapter) {
                    view = invokeHandler(((RequestHandlerAdapter) handler).asRequestHandler());
                } else {
                    view = invokeHandler(handler);
                }
                if (view != null) {
                    viewHolder.setView(view);
                    break;
                }
            }
        }

        private Asta4dView invokeHandler(Object handler) throws InvocationTargetException, IllegalAccessException, DataOperationException {
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
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof ForwardableException) {
                    throw (ForwardableException) e.getTargetException();
                }
                throw e;
            }
            if (result == null) {
                return null;
            } else if (result instanceof ForwardDescriptor) {
                return getForwardPageView(forwardDescriptors, (ForwardDescriptor) result);
            } else if (result instanceof String) {
                return new WebPageView((String) result);
            } else if (result instanceof RedirectView) {
                return ((RedirectView) result);
            }
            throw new UnsupportedOperationException("Result Type:" + result.getClass().getName());
        }
    }

    private static class ViewAndHandlersHolder {

        private ViewHolder viewHolder;

        private final List<Object> handlers;

        ViewAndHandlersHolder(List<Object> handlers) {
            this.viewHolder = new ViewHolder();
            this.handlers = handlers;
        }

        public ViewHolder getViewHolder() {
            return viewHolder;
        }

        public List<Object> getHandlers() {
            return handlers;
        }
    }
}
