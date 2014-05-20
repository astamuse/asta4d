package com.astamuse.asta4d.sample.handler.common;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.intelligent.CommonFormResult;

public class OneStepFormHandler<T> extends CommonFormHandler<T> {

    public OneStepFormHandler(Class<T> formCls) {
        super(formCls);
    }

    @RequestHandler
    public CommonFormResult handle() throws Exception {
        return handleWithCommonFormResult();
    }

}
