package com.astamuse.asta4d.sample.handler.common;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

public class CommonMultiPageStepFormHandler<T> extends CommonFormHandler<T> {

    private String templatePrefix;

    public CommonMultiPageStepFormHandler(Class<T> formCls, String templatePrefix) {
        super(formCls);
        this.templatePrefix = templatePrefix;
    }

    @RequestHandler
    public String handle() throws Exception {
        return createTemplateFilePathForStep(super.handleWithRenderTargetResult());
    }

    protected String createTemplateFilePathForStep(String step) {
        return templatePrefix + step + ".html";
    }

}
