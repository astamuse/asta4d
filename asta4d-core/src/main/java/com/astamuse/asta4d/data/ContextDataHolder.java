package com.astamuse.asta4d.data;

import java.io.Serializable;

public class ContextDataHolder<T> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String name;

    private String scope;

    private T value;

    private Class<T> typeCls;

    public ContextDataHolder() {
        super();
    }

    public ContextDataHolder(Class<T> typeCls) {
        super();
        this.typeCls = typeCls;
    }

    public ContextDataHolder(String name, String scope, T value) {
        super();
        this.name = name;
        this.scope = scope;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Class<T> getTypeCls() {
        return typeCls;
    }

}
