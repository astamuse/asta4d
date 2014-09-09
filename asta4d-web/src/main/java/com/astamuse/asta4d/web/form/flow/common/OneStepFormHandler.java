package com.astamuse.asta4d.web.form.flow.common;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.flow.base.AbstractFormFlowHandler;
import com.astamuse.asta4d.web.form.flow.base.CommonFormResult;

public abstract class OneStepFormHandler<T> extends AbstractFormFlowHandler<T> {

    public OneStepFormHandler(Class<T> formCls) {
        super(formCls);
    }

    @RequestHandler
    public CommonFormResult handle() throws Exception {
        return handleWithCommonFormResult();
    }

}
