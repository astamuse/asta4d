package com.astamuse.asta4d.web.test.render.base;

import com.astamuse.asta4d.test.render.infra.SimpleCase;

public class WebRenderCase extends SimpleCase {

    public WebRenderCase(String templateFileName) throws Throwable {
        super(templateFileName);
    }

    public WebRenderCase(String templateFileName, String confirmFileName) throws Throwable {
        super(templateFileName, confirmFileName);
    }

    @Override
    protected String retrieveTempateFielParentPath() {
        return "/com/astamuse/asta4d/web/test/render/templates/";
    }

    @Override
    protected String retrieveConfirmFielParentPath() {
        return "/com/astamuse/asta4d/web/test/render/confirms/";
    }

}
