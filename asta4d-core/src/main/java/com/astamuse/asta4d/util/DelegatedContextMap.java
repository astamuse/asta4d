/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
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
