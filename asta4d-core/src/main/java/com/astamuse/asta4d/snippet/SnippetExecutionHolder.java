package com.astamuse.asta4d.snippet;

import java.lang.reflect.Method;

import com.astamuse.asta4d.render.Renderer;

public class SnippetExecutionHolder extends SnippetExcecutionInfo {

    private Object[] params = null;

    private Renderer executeResult = null;

    public SnippetExecutionHolder(SnippetDeclarationInfo declarationInfo, Object instance, Method method, Object[] params,
            Renderer executeResult) {
        super(declarationInfo, instance, method);
        this.params = params;
        this.executeResult = executeResult;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Renderer getExecuteResult() {
        return executeResult;
    }

    public void setExecuteResult(Renderer executeResult) {
        this.executeResult = executeResult;
    }

}
