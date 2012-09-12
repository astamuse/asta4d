package com.astamuse.asta4d.snippet;

import java.lang.reflect.Method;

import com.astamuse.asta4d.render.Renderer;

public class SnippetExecutionHolder {

    private SnippetDeclarationInfo declarationInfo;

    private Object instance = null;

    private Method method = null;

    private Object[] params = null;

    private Renderer executeResult = null;

    public SnippetExecutionHolder() {

    }

    public SnippetExecutionHolder(SnippetDeclarationInfo declarationInfo, Object instance, Method method, Object[] params,
            Renderer executeResult) {
        super();
        this.declarationInfo = declarationInfo;
        this.instance = instance;
        this.method = method;
        this.params = params;
        this.executeResult = executeResult;
    }

    public SnippetDeclarationInfo getDeclarationInfo() {
        return declarationInfo;
    }

    public void setDeclarationInfo(SnippetDeclarationInfo declarationInfo) {
        this.declarationInfo = declarationInfo;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
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
