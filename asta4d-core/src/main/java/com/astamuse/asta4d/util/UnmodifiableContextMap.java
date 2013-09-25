package com.astamuse.asta4d.util;

import com.astamuse.asta4d.ContextMap;

public class UnmodifiableContextMap implements ContextMap {

    private ContextMap map;

    public UnmodifiableContextMap(ContextMap map) {
        super();
        this.map = map;
    }

    public void put(String key, Object data) {
        throw new UnsupportedOperationException("Put operation is forbidden on this class:" + this.getClass().getName());
    }

    public <T> T get(String key) {
        return map.get(key);
    }

    public ContextMap createClone() {
        return this;
    }

}
