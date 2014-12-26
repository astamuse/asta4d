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
