package com.astamuse.asta4d.snippet;

import java.lang.reflect.Method;

public class SnippetExcecutionInfo {

    protected SnippetDeclarationInfo declarationInfo;
    protected Object instance = null;
    protected Method method = null;

    public SnippetExcecutionInfo(SnippetDeclarationInfo declarationInfo, Object instance, Method method) {
        super();
        this.declarationInfo = declarationInfo;
        this.instance = instance;
        this.method = method;
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

}
