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

package com.astamuse.asta4d.snippet;

import java.lang.reflect.Method;

public class SnippetExcecutionInfo {

    protected SnippetDeclarationInfo declarationInfo;
    protected Object instance = null;
    protected Method method = null;

    public SnippetExcecutionInfo(SnippetDeclarationInfo declarationInfo, Object instance, Method method) {
        super();
        this.declarationInfo = declarationInfo;
        this.instance = instance;
        this.method = method;
    }

    public SnippetDeclarationInfo getDeclarationInfo() {
        return declarationInfo;
    }

    public void setDeclarationInfo(SnippetDeclarationInfo declarationInfo) {
        this.declarationInfo = declarationInfo;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

}
