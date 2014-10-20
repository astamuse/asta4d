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

package com.astamuse.asta4d;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.data.ContextBindData;
import com.astamuse.asta4d.data.ContextDataHolder;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.extnode.ExtNodeConstants;
import com.astamuse.asta4d.util.DelegatedContextMap;

public class Context {

    public final static String SCOPE_GLOBAL = "global";

    public final static String SCOPE_DEFAULT = "default";

    public final static String SCOPE_ATTR = "attr";

    public final static String SCOPE_EXT_ATTR = "ext_attr";

    private final static String KEY_CURRENT_LOCALE = "current_locale";

    private final static String KEY_CURRENT_RENDERING_ELEMENT = "current_rendering_element";

    private final static ThreadLocal<Context> instanceHolder = new ThreadLocal<>();

    private final static ContextMap globalMap = DelegatedContextMap.createBySingletonConcurrentHashMap();

    // this map is not thought to be used in multi threads since the instance of
    // Context is thread single.
    private Map<String, ContextMap> scopeMap = new HashMap<>();

    // private List

    @SuppressWarnings("unchecked")
    public final static <T extends Context> T getCurrentThreadContext() {
        return (T) instanceHolder.get();
    }

    public final static void setCurrentThreadContext(Context context) {
        instanceHolder.set(context);
    }

    public void setCurrentRenderingElement(Element elem) {
        setData(KEY_CURRENT_RENDERING_ELEMENT, elem);
    }

    public Element getCurrentRenderingElement() {
        return getData(KEY_CURRENT_RENDERING_ELEMENT);
    }

    public void setData(String key, Object data) {
        setData(SCOPE_DEFAULT, key, data);
    }

    public void setData(String scope, String key, Object data) {
        ContextMap dataMap = acquireMapForScope(scope);
        dataMap.put(key, data);
    }

    public <T> T getData(String key) {
        return getData(SCOPE_DEFAULT, key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(String scope, String key) {
        if (scope.equals(SCOPE_ATTR)) {
            return (T) retrieveElementAttr(key);
        } else {
            ContextMap dataMap = acquireMapForScope(scope);
            return (T) dataMap.get(key);
        }
    }

    public <T> ContextDataHolder<T> getDataHolder(String key) {
        return getDataHolder(SCOPE_DEFAULT, key);
    }

    @SuppressWarnings("unchecked")
    public <T> ContextDataHolder<T> getDataHolder(String scope, String key) {
        Object v = getData(scope, key);
        if (v == null) {
            return null;
        } else {
            ContextDataHolder<T> holder = new ContextDataHolder<T>(key, scope, (T) v);
            return holder;
        }
    }

    public Locale getCurrentLocale() {
        Locale currentLocale = getData(KEY_CURRENT_LOCALE);
        if (currentLocale != null) {
            return currentLocale;
        }
        return Locale.getDefault();
    }

    public void setCurrentLocale(Locale locale) {
        setData(KEY_CURRENT_LOCALE, locale);
    }

    private Object retrieveElementAttr(String key) {
        String dataRef = ExtNodeConstants.ATTR_DATAREF_PREFIX_WITH_NS + key;
        Element elem = getCurrentRenderingElement();
        Object value = null;
        while (value == null && elem != null) {
            // for a faked snippet node, we will just jump over it
            if (elem.tagName().equals(ExtNodeConstants.SNIPPET_NODE_TAG)) {
                String type = elem.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE);
                if (type.equals(ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE_FAKE)) {
                    elem = elem.parent();
                    continue;
                }
            }
            if (elem.hasAttr(dataRef)) {
                String id = elem.attr(dataRef);
                value = getData(SCOPE_EXT_ATTR, id);
            } else if (elem.hasAttr(key)) {
                value = elem.attr(key);
            }
            elem = elem.parent();
        }
        return value;
    }

    protected final ContextMap acquireMapForScope(String scope) {
        ContextMap dataMap = scopeMap.get(scope);
        if (dataMap == null) {
            dataMap = createMapForScope(scope);
            if (dataMap == null) {
                dataMap = DelegatedContextMap.createByNonThreadSafeHashMap();
            }
            scopeMap.put(scope, dataMap);
        }
        return dataMap;
    }

    /**
     * sub class can override this method for custom scope map instance
     * 
     * @param scope
     * @return
     */
    protected ContextMap createMapForScope(String scope) {
        ContextMap map = null;
        switch (scope) {
        case SCOPE_GLOBAL:
            map = globalMap;
            break;
        case SCOPE_EXT_ATTR:
            map = DelegatedContextMap.createBySingletonConcurrentHashMap();
            break;
        default:
            map = DelegatedContextMap.createByNonThreadSafeHashMap();
        }
        return map;
    }

    public void init() {
        clear();
        ContextBindData.initConext(this);
        InjectUtil.initContext(this);
    }

    public void clear() {
        scopeMap.clear();
    }

    public Context clone() {
        Context newCtx = new Context();
        copyScopesTo(newCtx);
        return newCtx;
    }

    protected void copyScopesTo(Context newCtx) {
        Set<Entry<String, ContextMap>> entrys = scopeMap.entrySet();
        for (Entry<String, ContextMap> entry : entrys) {
            newCtx.scopeMap.put(entry.getKey(), entry.getValue().createClone());
        }
    }

    public final static void with(Context context, Runnable runner) {
        Context oldContext = Context.getCurrentThreadContext();
        try {
            Context.setCurrentThreadContext(context);
            runner.run();
        } finally {
            Context.setCurrentThreadContext(oldContext);
        }
    }

    public final static <T> T with(Context context, Callable<T> caller) throws Exception {
        Context oldContext = Context.getCurrentThreadContext();
        try {
            Context.setCurrentThreadContext(context);
            return caller.call();
        } finally {
            Context.setCurrentThreadContext(oldContext);
        }
    }

    public void with(String varName, Object varValue, Runnable runner) {
        Object orinialData = null;
        try {
            orinialData = this.getData(varName);
            this.setData(varName, varValue);
            runner.run();
        } finally {
            // revive the scene
            this.setData(varName, orinialData);
        }
    }

    public <T> T with(String varName, Object varValue, Callable<T> caller) throws Exception {
        Object orinialData = null;
        try {
            orinialData = this.getData(varName);
            this.setData(varName, varValue);
            return caller.call();
        } finally {
            // revive the scene
            this.setData(varName, orinialData);
        }
    }

}
