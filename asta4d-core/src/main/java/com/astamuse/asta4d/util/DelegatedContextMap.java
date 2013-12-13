package com.astamuse.asta4d.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.astamuse.asta4d.ContextMap;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DelegatedContextMap implements ContextMap {

    private Class<? extends Map> mapCls;

    private Map map;

    private boolean copyOnClone;

    public DelegatedContextMap(Class<? extends Map> mapCls, boolean copyOnClone) {
        try {
            this.mapCls = mapCls;
            this.map = mapCls.newInstance();
            this.copyOnClone = copyOnClone;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final static DelegatedContextMap createBySingletonConcurrentHashMap() {
        return new DelegatedContextMap(ConcurrentHashMap.class, false);
    }

    public final static DelegatedContextMap createByNonThreadSafeHashMap() {
        return new DelegatedContextMap(HashMap.class, true);
    }

    @Override
    public void put(String key, Object data) {
        if (data == null) {
            map.remove(key);
        } else {
            map.put(key, data);
        }
    }

    @Override
    public <T> T get(String key) {
        return (T) map.get(key);
    }

    @Override
    public ContextMap createClone() {
        if (copyOnClone) {
            DelegatedContextMap newMap = new DelegatedContextMap(mapCls, copyOnClone);
            newMap.map.putAll(map);
            return newMap;
        } else {
            return this;
        }
    }
}
