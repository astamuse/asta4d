package com.astamuse.asta4d;

public interface ContextMap {

    public void put(String key, Object data);

    public <T> T get(String key);

    public ContextMap createClone();
}
