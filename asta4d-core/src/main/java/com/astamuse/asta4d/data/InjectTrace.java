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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.astamuse.asta4d.Context;

@SuppressWarnings("rawtypes")
public class InjectTrace {

    private static class TraceMap extends HashMap<String, ContextDataHolder>implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        Object targetInstance;

    }

    private static class InstanceTraceList extends LinkedList<TraceMap>implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

    }

    private static final String InstanceTraceListSaveKey = InjectTrace.class.getName() + "#InstanceTraceListSaveKey";

    public static final void saveInstanceInjectionTraceInfo(Object instance, Method setter, ContextDataHolder valueHolder) {
        saveInstanceInjectionTraceInfoInner(instance, createTraceKey(setter), valueHolder);
    }

    public static final void saveInstanceInjectionTraceInfo(Object instance, Field field, ContextDataHolder valueHolder) {
        saveInstanceInjectionTraceInfoInner(instance, createTraceKey(field), valueHolder);
    }

    public static final void saveInstanceInjectionTraceInfo(Object instance, String propertyName, ContextDataHolder valueHolder) {
        saveInstanceInjectionTraceInfoInner(instance, "pn:" + propertyName, valueHolder);
    }

    public static final void saveMethodInjectionTraceInfo(Method method, int parameterIndex, ContextDataHolder valueHolder) {
        saveInstanceInjectionTraceInfoInner(null, createTraceKey(method, parameterIndex), valueHolder);
    }

    private static final void saveInstanceInjectionTraceInfoInner(Object instance, String traceKey, ContextDataHolder valueHolder) {
        Context context = Context.getCurrentThreadContext();
        InstanceTraceList traceList = context.getData(InstanceTraceListSaveKey);
        if (traceList == null) {
            traceList = new InstanceTraceList();
            context.setData(InstanceTraceListSaveKey, traceList);
        }
        synchronized (traceList) {
            TraceMap traceMap = null;
            for (TraceMap map : traceList) {
                if (map.targetInstance == instance) {
                    traceMap = map;
                    break;
                }
            }
            if (traceMap == null) {
                traceMap = new TraceMap();
                traceMap.targetInstance = instance;
                traceList.add(traceMap);
            }

            traceMap.put(traceKey, valueHolder);
        }
    }

    public static final ContextDataHolder getInstanceInjectionTraceInfo(Object instance, Method setter) {
        return getInstanceInjectionTraceInfoInner(instance, createTraceKey(setter));
    }

    public static final ContextDataHolder getInstanceInjectionTraceInfo(Object instance, Field field) {
        return getInstanceInjectionTraceInfoInner(instance, createTraceKey(field));
    }

    public static final ContextDataHolder getInstanceInjectionTraceInfo(Object instance, String propertyName) {
        return getInstanceInjectionTraceInfoInner(instance, "pn:" + propertyName);
    }

    public static final ContextDataHolder getMethodInjectionTraceInfo(Method method, int parameterIndex) {
        return getInstanceInjectionTraceInfoInner(null, createTraceKey(method, parameterIndex));
    }

    private static final ContextDataHolder getInstanceInjectionTraceInfoInner(Object instance, String traceKey) {
        Context context = Context.getCurrentThreadContext();
        InstanceTraceList traceList = context.getData(InstanceTraceListSaveKey);
        if (traceList == null) {
            return null;
        }
        synchronized (traceList) {
            TraceMap traceMap = null;
            for (TraceMap map : traceList) {
                if (map.targetInstance == instance) {
                    traceMap = map;
                    break;
                }
            }
            if (traceMap == null) {
                return null;
            }

            return traceMap.get(traceKey);
        }
    }

    private static final String createTraceKey(Method m) {
        return m.getDeclaringClass().getName() + ":" + m.toString();
    }

    private static final String createTraceKey(Field f) {
        return f.getDeclaringClass().getName() + ":" + f.toString();
    }

    private static final String createTraceKey(Method m, int parameterIndex) {
        return m.getDeclaringClass().getName() + ":" + m.toString() + ":" + parameterIndex;
    }

    public static final List retrieveTraceList() {
        Context context = Context.getCurrentThreadContext();
        return context.getData(InstanceTraceListSaveKey);
    }

    public static final void restoreTraceList(List restoreList) {
        if (restoreList == null) {
            return;
        }
        for (TraceMap map : (InstanceTraceList) restoreList) {
            for (Entry<String, ContextDataHolder> entry : map.entrySet()) {
                saveInstanceInjectionTraceInfo(map.targetInstance, entry.getKey(), entry.getValue());
            }
        }
    }

}
