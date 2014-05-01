package com.astamuse.asta4d.data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.astamuse.asta4d.Context;

@SuppressWarnings("rawtypes")
public class InjectTrace {

    private static class TraceMap extends HashMap<String, ContextDataHolder> {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        Object targetInstance;

    }

    private static class InstanceTraceList extends LinkedList<TraceMap> {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

    }

    private static final String InstanceTraceListSaveKey = InjectTrace.class.getName() + "#InstanceTraceListSaveKey";

    public static final void saveInstanceInjectionTraceInfo(Object instance, Method setter, ContextDataHolder valueHolder) {
        saveInstanceInjectionTraceInfo(instance, createTraceKey(setter), valueHolder);
    }

    public static final void saveInstanceInjectionTraceInfo(Object instance, Field field, ContextDataHolder valueHolder) {
        saveInstanceInjectionTraceInfo(instance, createTraceKey(field), valueHolder);
    }

    public static final void saveMethodInjectionTraceInfo(Method method, int parameterIndex, ContextDataHolder valueHolder) {
        saveInstanceInjectionTraceInfo(null, createTraceKey(method, parameterIndex), valueHolder);
    }

    private static final void saveInstanceInjectionTraceInfo(Object instance, String traceKey, ContextDataHolder valueHolder) {
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
        return getInstanceInjectionTraceInfo(instance, createTraceKey(setter));
    }

    public static final ContextDataHolder getInstanceInjectionTraceInfo(Object instance, Field field) {
        return getInstanceInjectionTraceInfo(instance, createTraceKey(field));
    }

    public static final ContextDataHolder getMethodInjectionTraceInfo(Method method, int parameterIndex) {
        return getInstanceInjectionTraceInfo(null, createTraceKey(method, parameterIndex));
    }

    private static final ContextDataHolder getInstanceInjectionTraceInfo(Object instance, String traceKey) {
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

    public static final void restoreTraceList(List list) {
        Context context = Context.getCurrentThreadContext();
        context.setData(InstanceTraceListSaveKey, list);
    }

}
