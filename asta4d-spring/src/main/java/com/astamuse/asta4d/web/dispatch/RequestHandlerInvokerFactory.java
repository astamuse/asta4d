package com.astamuse.asta4d.web.dispatch;

import java.util.Collections;
import java.util.List;

import com.astamuse.asta4d.web.interceptor.RequestHandlerInterceptor;

public class RequestHandlerInvokerFactory {

    private RequestHandlerInvoker invoker = new RequestHandlerInvoker();

    private List<RequestHandlerInterceptor> interceptorList = Collections.emptyList();

    RequestHandlerInvoker getInvoker() {
        return invoker;
    }

    public void setInvoker(RequestHandlerInvoker invoker) {
        this.invoker = invoker;
        this.invoker.setInterceptorList(Collections.unmodifiableList(interceptorList));
    }

    public void setInterceptorList(List<RequestHandlerInterceptor> interceptorList) {
        this.interceptorList = interceptorList;
        this.invoker.setInterceptorList(Collections.unmodifiableList(interceptorList));
    }
}
