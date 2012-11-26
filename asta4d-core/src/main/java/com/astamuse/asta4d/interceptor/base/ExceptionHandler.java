package com.astamuse.asta4d.interceptor.base;

public class ExceptionHandler {

    private Exception exception;

    public ExceptionHandler() {
    }

    public ExceptionHandler(Exception exception) {
        super();
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

}
