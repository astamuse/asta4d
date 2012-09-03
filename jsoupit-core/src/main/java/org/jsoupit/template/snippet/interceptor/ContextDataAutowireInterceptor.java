package org.jsoupit.template.snippet.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.jsoupit.template.Context;
import org.jsoupit.template.render.Renderer;
import org.jsoupit.template.snippet.SnippetInfo;
import org.jsoupit.template.snippet.SnippetInvokeException;

public class ContextDataAutowireInterceptor implements SnippetInterceptor {

    private final static String InstanceCacheListKey = ContextDataAutowireInterceptor.class + "##InstanceCacheListKey##";

    private static class TargetInfo {
        String name;
        String scope;
        Class<?> type;
        Object defaultValue;

        void fixForPrimitiveType() {
            if (type.isPrimitive()) {
                String name = type.getName();
                switch (name) {
                case "char":
                    type = Character.class;
                    defaultValue = ' ';
                    break;
                case "byte":
                    type = Byte.class;
                    defaultValue = new Byte((byte) 0);
                    break;
                case "short":
                    type = Short.class;
                    defaultValue = new Short((short) 0);
                    break;
                case "int":
                    type = Integer.class;
                    defaultValue = new Integer(0);
                    break;
                case "long":
                    type = Long.class;
                    defaultValue = new Long(0L);
                    break;
                }
            } else {
                defaultValue = null;
            }
        }
    }

    private static class MethodInfo extends TargetInfo {
        Method method;
    }

    private static class FieldInfo extends TargetInfo {
        Field field;
    }

    private static class AutoWireTarget {
        List<FieldInfo> fieldList = new ArrayList<>();
        List<MethodInfo> setMethodList = new ArrayList<>();
        List<MethodInfo> getMethodList = new ArrayList<>();
    }

    private final static ConcurrentHashMap<SnippetInfo, AutoWireTarget> TargetCache = new ConcurrentHashMap<>();

    private List<ContextDataConvertor> convertorList = null;

    // TODO reverse wire to Context
    @Override
    public Renderer beforeSnippet(SnippetInfo snippetInfo, Object instance, Method method) throws SnippetInvokeException {
        // instance.getClass().get
        List<Object> cachedInstanceList = getCachedSnippetInstanceList();
        boolean found = false;
        for (Object object : cachedInstanceList) {
            found = object == instance;
            if (found)
                break;
        }
        if (!found) {
            cachedInstanceList.add(instance);
            try {
                fillData(snippetInfo, instance);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new SnippetInvokeException(e);
            }
        }
        return null;
    }

    private void fillData(SnippetInfo snippetInfo, Object instance) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        // TODO type converting
        AutoWireTarget target = getTarget(snippetInfo, instance);
        Object value;
        for (FieldInfo fi : target.fieldList) {
            value = getAppropriateData(fi.scope, fi.name, fi.type);
            FieldUtils.writeField(fi.field, instance, value, true);
        }

        for (MethodInfo mi : target.setMethodList) {
            value = getAppropriateData(mi.scope, mi.name, mi.type);
            if (value == null && mi.defaultValue != null) {
                value = mi.defaultValue;
            }
            mi.method.invoke(instance, value);
        }
    }

    @Override
    public void afterSnippet(SnippetInfo snippetInfo, Object instance, Method method) {
        // TODO reverse wiring

    }

    private Object getAppropriateData(String scope, String name, Class<?> type) {
        Object v = getData(scope, name);
        if (v == null) {
            return null;
        }

        if (type.isAssignableFrom(v.getClass())) {
            return v;
        }

        ContextDataConvertor convertor = getConvertor(v.getClass(), type);
        if (convertor == null) {
            // TODO what to do?
            return null;
        } else {
            return convertor.convert(v);
        }

    }

    protected Object getData(String scope, String name) {
        if (scope == null || !scope.equals("context")) {
            return null;
        }
        Context context = Context.getCurrentThreadContext();
        return context.getData(name);
    }

    private List<Object> getCachedSnippetInstanceList() {
        Context context = Context.getCurrentThreadContext();
        List<Object> list = context.getData(InstanceCacheListKey);
        if (list == null) {
            list = new ArrayList<>();
            context.setData(InstanceCacheListKey, list);
        }
        return list;
    }

    private AutoWireTarget getTarget(SnippetInfo snippetInfo, Object instance) {
        boolean cacheEnable = Context.getCurrentThreadContext().getConfiguration().isCacheEnable();
        AutoWireTarget target = null;
        if (cacheEnable) {
            target = TargetCache.get(snippetInfo);
            if (target == null) {
                target = createTarget(instance);
                TargetCache.put(snippetInfo, target);
            }
        } else {
            target = createTarget(instance);
        }
        return target;
    }

    private AutoWireTarget createTarget(Object instance) {
        AutoWireTarget target = new AutoWireTarget();
        Class<?> cls = instance.getClass();
        ContextData cd;
        // TODO we should use class name to confirm contextdata annotation
        // because they are possibly from different class loader
        Method[] mtds = cls.getMethods();
        for (Method method : mtds) {
            if (method.isAnnotationPresent(ContextData.class)) {
                cd = method.getAnnotation(ContextData.class);
                MethodInfo mi = new MethodInfo();

                mi.method = method;

                boolean isGet = false;
                boolean isSet = false;
                if (cd.value().isEmpty()) {
                    String name = method.getName();
                    if (name.startsWith("set")) {
                        name = name.substring(3);
                        isSet = true;
                    } else if (name.startsWith("get")) {
                        name = name.substring(3);
                        isGet = true;
                    } else if (name.startsWith("is")) {
                        name = name.substring(2);
                        isSet = true;
                    } else {
                        if (method.getParameterTypes().length == 0) {
                            isGet = true;
                        } else {
                            isSet = true;
                        }
                    }
                    char[] cs = name.toCharArray();
                    cs[0] = Character.toLowerCase(cs[0]);
                    mi.name = new String(cs);

                } else {
                    mi.name = cd.value();
                    if (method.getParameterTypes().length == 0) {
                        isGet = true;
                    } else {
                        isSet = true;
                    }
                }
                mi.scope = cd.scope();

                if (isGet) {
                    mi.type = method.getReturnType();
                    mi.fixForPrimitiveType();
                    target.getMethodList.add(mi);
                }

                if (isSet) {
                    mi.type = method.getParameterTypes()[0];
                    mi.fixForPrimitiveType();
                    target.setMethodList.add(mi);
                }

            }
        }

        String objCls = Object.class.getName();
        Field[] flds;
        FieldInfo fi;
        while (!cls.getName().equals(objCls)) {
            flds = cls.getDeclaredFields();
            for (Field field : flds) {
                if (field.isAnnotationPresent(ContextData.class)) {
                    fi = new FieldInfo();
                    fi.field = field;
                    fi.type = field.getType();
                    cd = field.getAnnotation(ContextData.class);
                    if (cd.value().isEmpty()) {
                        fi.name = field.getName();
                    } else {
                        fi.name = cd.value();
                    }
                    fi.scope = cd.scope();
                    fi.fixForPrimitiveType();
                    target.fieldList.add(fi);
                }
            }
            cls = cls.getSuperclass();
        }

        return target;
    }

    protected ContextDataConvertor getConvertor(Class<?> srcType, Class<?> targetType) {
        for (ContextDataConvertor convertor : convertorList) {
            if (convertor.getSourceType().isAssignableFrom(srcType) && targetType.isAssignableFrom(convertor.getTargetType())) {
                return convertor;
            }
        }
        return null;
    }

    public void setConvertorList(List<ContextDataConvertor> convertorList) {
        this.convertorList = convertorList;

    }

}
