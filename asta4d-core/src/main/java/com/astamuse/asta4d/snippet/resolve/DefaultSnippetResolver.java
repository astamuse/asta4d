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

package com.astamuse.asta4d.snippet.resolve;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.snippet.SnippetDeclarationInfo;
import com.astamuse.asta4d.snippet.SnippetExcecutionInfo;
import com.astamuse.asta4d.snippet.SnippetNotResovlableException;
import com.astamuse.asta4d.util.MultiSearchPathResourceLoader;

public class DefaultSnippetResolver extends MultiSearchPathResourceLoader<Object> implements SnippetResolver {

    private final static String InstanceMapCacheKey = DefaultSnippetResolver.class.getName() + "##InstanceMapCacheKey";

    private final static ConcurrentHashMap<SnippetDeclarationInfo, Method> MethodCache = new ConcurrentHashMap<>();

    @Override
    public SnippetExcecutionInfo resloveSnippet(SnippetDeclarationInfo declaration) throws SnippetNotResovlableException {
        Object instance = retrieveInstance(declaration);
        Method method = retrieveMethod(declaration);
        return new SnippetExcecutionInfo(declaration, instance, method);
    }

    protected Object retrieveInstance(SnippetDeclarationInfo declaration) throws SnippetNotResovlableException {
        String snippetName = declaration.getSnippetName();
        Map<String, Object> instanceMap = getCacheMap(InstanceMapCacheKey);
        Object instance = instanceMap.get(snippetName);
        if (instance == null) {
            instance = createInstance(snippetName);
            instanceMap.put(snippetName, instance);
        }
        return instance;
    }

    protected Object createInstance(String snippetName) throws SnippetNotResovlableException {
        try {
            Object instance = super.searchResource(".", snippetName);
            if (instance == null) {
                throw new ClassNotFoundException("Can not found class for snippet name:" + snippetName);
            }
            return instance;
        } catch (Exception ex) {
            throw new SnippetNotResovlableException(String.format("Snippet [%s] resolve failed.", snippetName), ex);
        }
    }

    @Override
    protected Object loadResource(String name) {
        Class<?> clz = null;
        try {
            clz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
        try {
            return clz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected Method retrieveMethod(SnippetDeclarationInfo declaration) throws SnippetNotResovlableException {
        Method m = Configuration.getConfiguration().isCacheEnable() ? MethodCache.get(declaration) : null;
        if (m == null) {
            Object instance = retrieveInstance(declaration);
            m = findSnippetMethod(instance, declaration.getSnippetHandler());
            if (m == null) {
                throw new SnippetNotResovlableException("Snippet handler cannot be resolved for " + declaration);
            }
            // we do not mind that the exited method instance would be
            // overrode in multi-threads environment
            MethodCache.put(declaration, m);
        }
        return m;
    }

    protected Method findSnippetMethod(Object snippetInstance, String methodName) {
        Method[] methodList = snippetInstance.getClass().getMethods();
        Class<?> rendererCls = Renderer.class;
        List<Method> namedMtdList = new ArrayList<>();
        List<Method> priorMtdList = new ArrayList<>();
        for (Method method : methodList) {
            if (method.getName().equals(methodName) && rendererCls.isAssignableFrom(method.getReturnType())) {
                namedMtdList.add(method);
                if (method.getAnnotation(PriorRenderMethod.class) != null) {
                    priorMtdList.add(method);
                }
            }
        }

        if (priorMtdList.isEmpty()) {
            if (namedMtdList.isEmpty()) {
                return null;
            } else {
                return namedMtdList.get(namedMtdList.size() - 1);
            }
        } else {
            return priorMtdList.get(priorMtdList.size() - 1);
        }
    }

    private Map<String, Object> getCacheMap(String key) {
        Context context = Context.getCurrentThreadContext();
        Map<String, Object> map = context.getData(key);
        if (map == null) {
            map = new HashMap<>();
            context.setData(key, map);
        }
        return map;
    }

}
