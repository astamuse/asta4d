package com.astamuse.asta4d.sample.handler.common;

import com.astamuse.asta4d.web.form.intelligent.IntelligentFormHandler;

public abstract class CommonFormHandler<T> extends IntelligentFormHandler<T> {

    public CommonFormHandler(Class<T> formCls) {
        super(formCls);
    }

}
