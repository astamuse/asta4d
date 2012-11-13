package com.astamuse.asta4d.web.dispatch;

import java.util.ArrayList;
import java.util.List;

import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.interceptor.base.Executor;
import com.astamuse.asta4d.interceptor.base.GenericInterceptor;
import com.astamuse.asta4d.interceptor.base.InterceptorUtil;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerInterceptor;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerResultHolder;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.util.AnnotationMethodHelper;

public class DefaultRequestHandlerInvoker implements RequestHandlerInvoker {

    /* (non-Javadoc)
     * @see com.astamuse.asta4d.web.dispatch.RequestHandlerInvoker#invoke(com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule)
     */
    @Override
    public Object invoke(UrlMappingRule rule) throws Exception {
        RequestHandlerInvokeExecutor executor = new RequestHandlerInvokeExecutor(rule.getHandlerList());
        RequestHandlerResultHolder holder = new RequestHandlerResultHolder();
        InterceptorUtil.executeWithInterceptors(holder, buildInterceptorList(rule), executor);
        return holder.getResult();
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

    private static class RequestHandlerInterceptorWrapper implements GenericInterceptor<RequestHandlerResultHolder> {

        private final UrlMappingRule rule;
        private final RequestHandlerInterceptor interceptor;

        public RequestHandlerInterceptorWrapper(UrlMappingRule rule, RequestHandlerInterceptor interceptor) {
            this.rule = rule;
            this.interceptor = interceptor;
        }

        @Override
        public boolean beforeProcess(RequestHandlerResultHolder holder) throws Exception {
            interceptor.preHandle(rule, holder);
            return holder.getResult() == null;
        }

        @Override
        public void afterProcess(RequestHandlerResultHolder holder, ExceptionHandler exceptionHandler) {
            interceptor.postHandle(rule, holder, exceptionHandler);
        }
    }

    private static class RequestHandlerInvokeExecutor implements Executor<RequestHandlerResultHolder> {

        private final List<Object> requestHandlerList;

        public RequestHandlerInvokeExecutor(List<Object> requestHandlerList) {
            this.requestHandlerList = requestHandlerList;
        }

        @Override
        public void execute(RequestHandlerResultHolder holder) throws Exception {
            Object requestHandlerResult = null;
            for (Object handler : requestHandlerList) {
                requestHandlerResult = AnnotationMethodHelper.invokeMethodForAnnotation(handler, RequestHandler.class);
                if (requestHandlerResult != null) {
                    holder.setResult(requestHandlerResult);
                    break;
                }
            }
            holder.setResult(requestHandlerResult);
        }

    }

}
