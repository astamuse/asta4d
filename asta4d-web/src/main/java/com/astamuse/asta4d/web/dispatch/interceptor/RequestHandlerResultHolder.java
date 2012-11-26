package com.astamuse.asta4d.web.dispatch.interceptor;

public class RequestHandlerResultHolder {

    private Object result;

    public RequestHandlerResultHolder() {
    }

    public RequestHandlerResultHolder(Object result) {
        super();
        this.result = result;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
