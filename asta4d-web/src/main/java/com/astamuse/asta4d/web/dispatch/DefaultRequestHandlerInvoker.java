/*
 * Copyright 2012 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.astamuse.asta4d.web.dispatch;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.interceptor.base.Executor;
import com.astamuse.asta4d.interceptor.base.GenericInterceptor;
import com.astamuse.asta4d.interceptor.base.InterceptorUtil;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerInterceptor;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerResultHolder;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;
import com.astamuse.asta4d.web.dispatch.request.ResultTransformerUtil;
import com.astamuse.asta4d.web.dispatch.response.provider.ContentProvider;
import com.astamuse.asta4d.web.util.bean.AnnotationMethodHelper;

public class DefaultRequestHandlerInvoker implements RequestHandlerInvoker {

    public static final String TRACE_VAR_CURRENT_HANDLER = "TRACE_VAR_CURRENT_HANDLER#" + DefaultRequestHandlerInvoker.class;

    /* (non-Javadoc)
     * @see com.astamuse.asta4d.web.dispatch.RequestHandlerInvoker#invoke(com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule)
     */
    @Override
    public List<ContentProvider> invoke(UrlMappingRule rule) throws Exception {
        RequestHandlerInvokeExecutor executor = new RequestHandlerInvokeExecutor(rule.getHandlerList(), rule.getResultTransformerList());
        RequestHandlerResultHolder holder = new RequestHandlerResultHolder();
        InterceptorUtil.executeWithInterceptors(holder, buildInterceptorList(rule), executor);
        return holder.getContentProviderList();
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
            return holder.getContentProviderList() == null;
        }

        @Override
        public void afterProcess(RequestHandlerResultHolder holder, ExceptionHandler exceptionHandler) {
            interceptor.postHandle(rule, holder, exceptionHandler);
        }
    }

    private static class RequestHandlerInvokeExecutor implements Executor<RequestHandlerResultHolder> {

        private final List<Object> requestHandlerList;

        private final List<ResultTransformer> resultTransformerList;

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        public RequestHandlerInvokeExecutor(List<Object> requestHandlerList, List<ResultTransformer> resultTransformerList) {
            this.requestHandlerList = requestHandlerList;
            this.resultTransformerList = resultTransformerList;
        }

        @Override
        public void execute(RequestHandlerResultHolder holder) throws Exception {
            List<ContentProvider> cpList = new ArrayList<>();
            Object result;
            ContentProvider cp;
            Context context = Context.getCurrentThreadContext();
            for (Object handler : requestHandlerList) {
                try {
                    context.setData(TRACE_VAR_CURRENT_HANDLER, handler);
                    result = AnnotationMethodHelper.invokeMethodForAnnotation(handler, RequestHandler.class);
                } catch (Throwable t) {
                    logger.error(t.getMessage(), t);
                    result = t;
                } finally {
                    context.setData(TRACE_VAR_CURRENT_HANDLER, null);
                }
                if (result != null) {
                    cp = ResultTransformerUtil.transform(result, resultTransformerList);
                    cpList.add(cp);
                    if (!cp.isContinuable()) {
                        break;
                    }
                }// result != null
            }// for
            if (cpList.isEmpty()) {
                cpList.add(ResultTransformerUtil.transform(null, resultTransformerList));
            }
            holder.setContentProviderList(cpList);
        }

    }

}
