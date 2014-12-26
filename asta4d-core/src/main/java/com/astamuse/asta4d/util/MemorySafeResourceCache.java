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

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * We want to cache the result of not found resources but a potential DDOS attack can be performed against this wish. Thus we cache the not
 * found results in a SoftRreferenced map.
 * 
 * @author e-ryu
 * 
 * @param <K>
 * @param <V>
 */
public class MemorySafeResourceCache<K, V> {

    public static class ResouceHolder<T> {
        private T resource;

        ResouceHolder(T _res) {
            resource = _res;
        }

        public T get() {
            return resource;
        }

        public boolean exists() {
            return resource != null;
        }
    }

    private Map<K, ResouceHolder<V>> existingResourceMap;

    private SoftReference<Map<K, ResouceHolder<V>>> notExistingResourceMapRef = null;

    private final ResouceHolder<V> notExistingHolder = new ResouceHolder<>(null);

    public MemorySafeResourceCache() {
        // a copy on write map would be better
        existingResourceMap = new ConcurrentHashMap<>();
    }

    /**
     * 
     * @param key
     *            throw NullPointerException when key is null
     * @param resource
     *            null means the resource of the given key is not existing
     */
    public void put(K key, V resource) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (resource == null) {
            getNotExistingResourceMap().put(key, notExistingHolder);
        } else {
            existingResourceMap.put(key, new ResouceHolder<>(resource));
        }
    }

    private Map<K, ResouceHolder<V>> getNotExistingResourceMap() {
        Map<K, ResouceHolder<V>> map = null;
        if (notExistingResourceMapRef == null) {
            map = new ConcurrentHashMap<>();
            notExistingResourceMapRef = new SoftReference<>(map);
        } else {
            map = notExistingResourceMapRef.get();
            if (map == null) {
                map = new ConcurrentHashMap<>();
                notExistingResourceMapRef = new SoftReference<>(map);
            }
        }
        return map;
    }

    public ResouceHolder<V> get(K key) {
        ResouceHolder<V> holder = existingResourceMap.get(key);
        if (holder == null) {
            holder = getNotExistingResourceMap().get(key);
        }
        return holder;
    }
}
