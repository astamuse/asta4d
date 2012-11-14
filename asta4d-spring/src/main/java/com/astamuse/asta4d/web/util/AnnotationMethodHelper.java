package com.astamuse.asta4d.web.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.data.InjectUtil;

public class AnnotationMethodHelper {

    private final static ConcurrentHashMap<String, Method> methodCache = new ConcurrentHashMap<>();

    private final static String getCacheKey(Class<?> cls, Class<? extends Annotation> annotation) {
        return cls.getName() + "###annotation##" + annotation.getName();
    }

    public final static Method findMethod(Object obj, Class<? extends Annotation> annotation) {
        Method m = null;
        String cacheKey = getCacheKey(obj.getClass(), annotation);
        if (Context.getCurrentThreadContext().getConfiguration().isCacheEnable()) {
            m = methodCache.get(cacheKey);
            if (m != null) {
                return m;
            }
        }

        m = findMethod(obj.getClass(), annotation);
        if (m != null) {
            methodCache.put(cacheKey, m);
        }
        return m;
    }

    private final static Method findMethod(Class<?> cls, Class<? extends Annotation> annotation) {
        if (cls.getName().equals(Object.class.getName())) {
            return null;
        }
        Method[] methodList = cls.getMethods();
        Method m = null;
        for (Method method : methodList) {
            if (method.isAnnotationPresent(annotation)) {
                m = method;
                break;
            }
        }
        if (m == null) {
            return findMethod(cls.getSuperclass(), annotation);
        } else {
            return m;
        }
    }

    public final static Object invokeMethodForAnnotation(Object obj, Class<? extends Annotation> annotation)
            throws InvocationTargetException, DataOperationException, IllegalAccessException, IllegalArgumentException {
        Object targetObj = obj instanceof DeclareInstanceAdapter ? ((DeclareInstanceAdapter) obj).asTargetInstance() : obj;
        Method m = findMethod(targetObj, annotation);
        if (m == null) {
            // TODO maybe we can return a null?
            String msg = String.format("Method not found for annotation %s at class %s:", annotation.toString(), targetObj.getClass()
                    .getName());
            throw new InvocationTargetException(new RuntimeException(msg));
        }

        Object[] params = InjectUtil.getMethodInjectParams(m);
        if (params == null) {
            params = new Object[0];
        }
        try {
            return m.invoke(targetObj, params);
        } catch (InvocationTargetException e) {
            throw e;
        }
    }
}
