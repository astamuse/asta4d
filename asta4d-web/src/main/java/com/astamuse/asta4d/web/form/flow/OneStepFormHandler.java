package com.astamuse.asta4d.web.form.flow;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.flow.common.CommonFormResult;
import com.astamuse.asta4d.web.form.flow.common.AbstractFlowFormHandler;

public class OneStepFormHandler<T> extends AbstractFlowFormHandler<T> {

    public OneStepFormHandler(Class<T> formCls) {
        super(formCls);
    }

    @RequestHandler
    public CommonFormResult handle() throws Exception {
        return handleWithCommonFormResult();
    }

}
