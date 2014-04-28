package com.astamuse.asta4d.sample.handler;

import com.astamuse.asta4d.web.form.IntelligentFormHandler;

public class CommonFormHandler<T> extends IntelligentFormHandler<T> {

    public CommonFormHandler(Class<T> formCls) {
        super(formCls);
    }

}
