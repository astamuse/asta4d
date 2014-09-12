/*
 * Copyright 2012 astamuse company,Ltd.
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

package com.astamuse.asta4d.data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.astamuse.asta4d.Context;

/**
 * a ContextBindData is supposed to be cache in the current thread Context and should only be initialized once
 * 
 * @author e-ryu
 * 
 * @param <T>
 */
public abstract class ContextBindData<T> {

    private final static String MapKey = "##MapKey##" + ContextBindData.class.getName();

    private static class DataWithLock {
        volatile Object data = null;
        volatile boolean valid = false;
        // final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    }

    private String bindKey = this.getClass().getName() + "##bind-key##23@23@@23";

    private boolean contextSynchronizable;

    /**
     * The default constructor is for convenience of lazy load data
     */
    public ContextBindData() {
        this(false);
    }

    /**
     * If contextSynchronizable is set to false, no synchronization operations will be performed and it is just a convenience for lazy load
     * data.
     * 
     * Otherwise, if contextSynchronizable is set to true, a Context based lock will be performed and the data will be load only once in the
     * current context environment.
     * 
     * @param doSynchronize
     */
    public ContextBindData(boolean contextSynchronizable) {
        this.contextSynchronizable = contextSynchronizable;
    }

    public final static void initConext(Context context) {
        ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
        context.setData(MapKey, map);
    }

    public T get() {
        if (contextSynchronizable) {
            return getDataSynchronously();
        } else {
            Context context = Context.getCurrentThreadContext();
            T data = context.getData(bindKey);
            if (data == null) {
                data = buildData();
                context.setData(bindKey, data);
            }
            return data;
        }

    }

    @SuppressWarnings("unchecked")
    private T getDataSynchronously() {
        Context context = Context.getCurrentThreadContext();
        ConcurrentMap<String, Object> map = context.getData(MapKey);

        // map will not be null since we have initialized it in constructor
        DataWithLock dl = (DataWithLock) map.get(bindKey);
        if (dl == null) {
            dl = new DataWithLock();
            DataWithLock prev = (DataWithLock) map.putIfAbsent(bindKey, dl);
            if (prev != null) {
                dl = prev;
            }
        }
        if (!dl.valid) {
            Object data;
            // TODO I want to read jvm code...
            synchronized (dl) {
                if (!dl.valid) {
                    data = dl.data;
                    if (data == null) {
                        data = buildData();
                        dl.data = data;
                    }
                    dl.valid = true;
                }
            }
        }
        return (T) dl.data;
    }

    protected abstract T buildData();
}
