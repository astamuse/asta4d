package com.astamuse.asta4d.sample.handler.common;


public abstract class MultiStepFormHandler<T> extends CommonFormHandler<T> {

    public MultiStepFormHandler(Class<T> formCls) {
        super(formCls);
    }

}
