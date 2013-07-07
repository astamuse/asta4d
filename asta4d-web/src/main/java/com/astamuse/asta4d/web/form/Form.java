package com.astamuse.asta4d.web.form;

import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.web.util.GlobalRenderingHelper;

@ContextDataSet
public abstract class Form {

    private String msgContainerSelector = null;

    private String msgRenderingSelector = null;

    public Form() {
        super();
    }

    public Form(String msgContainerSelector, String msgRenderingSelector) {
        super();
        this.msgContainerSelector = msgContainerSelector;
        this.msgRenderingSelector = msgRenderingSelector;
    }

    public abstract boolean validationOK();

    public void addValidationMsg(String msg) {
        String container = this.msgContainerSelector;
        if (container == null) {
            container = GlobalRenderingHelper.DefaultGlobalContainerSelector;
        }
        addValidationMsg(container, msg);
    }

    public void addValidationMsg(String itemContainerSelector, String msg) {
        String renderSelector = this.msgRenderingSelector;
        if (renderSelector == null) {
            renderSelector = "*";
        }
        addValidationMsg(itemContainerSelector, renderSelector, msg);
    }

    public void addValidationMsg(String itemContainerSelector, String msgRenderingSelector, String msg) {
        addValidationMsg(itemContainerSelector, Renderer.create(msgRenderingSelector, msg));
    }

    public void addValidationMsg(String itemContainerSelector, Renderer msgRenderer) {
        GlobalRenderingHelper.addRenderer(itemContainerSelector, msgRenderer);
    }
}
