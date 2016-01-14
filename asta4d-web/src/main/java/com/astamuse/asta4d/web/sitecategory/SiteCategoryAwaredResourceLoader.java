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
package com.astamuse.asta4d.web.sitecategory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.util.MemorySafeResourceCache;
import com.astamuse.asta4d.util.MemorySafeResourceCache.ResouceHolder;

/**
 * 
 * We will cache the existing path only instead of the actual found resource, the cache of resources is assumed to be performed at the
 * underline load mechanism.
 * 
 * @author e-ryu
 * 
 * @param <T>
 */
public abstract class SiteCategoryAwaredResourceLoader<T> {

    private static class CacheKey {
        String category;
        String path;

        static CacheKey of(String category, String path) {
            CacheKey key = new CacheKey();
            key.category = category;
            key.path = path;
            return key;
        }

        @Override
        public int hashCode() {
            return path.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            CacheKey other = (CacheKey) obj;
            // category and path would not be null
            return category.equals(other.category) && path.equals(other.path);
        }

    }

    private MemorySafeResourceCache<CacheKey, String> existingPathCache = new MemorySafeResourceCache<>();

    public SiteCategoryAwaredResourceLoader() {

    }

    /**
     * 
     * @param path
     * @return null means the target resource is not found
     * @throws Exception
     */
    public abstract T load(String path, Object extraInfomation) throws Exception;

    public T load(String[] categories, String path) throws Exception {
        return load(categories, path, null);
    }

    public T load(String[] categories, String path, Object extraInfomation) throws Exception {
        boolean cacheEnable = Configuration.getConfiguration().isCacheEnable();
        ResouceHolder<String> existingPath = null;
        CacheKey key;
        for (String category : categories) {
            key = CacheKey.of(category, path);
            existingPath = cacheEnable ? existingPathCache.get(key) : null;
            if (existingPath == null) {// not check yet
                String tryPath = createCategorySpecialPath(category, path);
                T res = load(tryPath, extraInfomation);
                if (res == null) {
                    existingPathCache.put(key, null);
                    continue;
                } else {
                    existingPathCache.put(key, tryPath);
                    return res;
                }
            } else if (existingPath.exists()) {
                return load(existingPath.get(), extraInfomation);
            } else {
                continue;
            }
        }
        // it also means not found
        return null;
    }

    protected String createCategorySpecialPath(String category, String path) {
        if (category.isEmpty()) {// category would not be null
            return path;
        } else {
            if (path.startsWith("/")) {
                return "/" + category + path;
            } else {
                return "/" + category + "/" + path;
            }
        }
    }

}
