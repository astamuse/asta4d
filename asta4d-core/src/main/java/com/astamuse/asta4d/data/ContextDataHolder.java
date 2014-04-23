package com.astamuse.asta4d.data;

import java.io.Serializable;

public class ContextDataHolder<T> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String name;

    private String scope;

    private Object foundOriginalData;

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
        this.foundOriginalData = value;
    }

    public String getName() {
        return name;
    }

    public String getScope() {
        return scope;
    }

    public Object getFoundOriginalData() {
        return foundOriginalData;
    }

    public T getValue() {
        return value;
    }

    public Class<T> getTypeCls() {
        return typeCls;
    }

    public void setData(String name, String scope, T value) {
        setData(name, scope, value, value);
    }

    public void setData(String name, String scope, Object foundValue, T transformedValue) {
        this.name = name;
        this.scope = scope;
        this.value = transformedValue;
        this.foundOriginalData = foundValue;
    }

}
