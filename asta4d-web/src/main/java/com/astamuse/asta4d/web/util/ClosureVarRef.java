package com.astamuse.asta4d.web.util;

public class ClosureVarRef<T> {

    private T data;

    public ClosureVarRef() {
        this.data = null;
    }

    public ClosureVarRef(T data) {
        this.data = data;
    }

    public void set(T data) {
        this.data = data;
    }

    public T get() {
        return this.data;
    }
}
