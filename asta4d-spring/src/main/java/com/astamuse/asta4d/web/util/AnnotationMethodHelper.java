package com.astamuse.asta4d.web.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.data.InjectUtil;

public class AnnotationMethodHelper {

    public final static Method findMethod(Object obj, Class<? extends Annotation> annotation) {
        // TODO cache the result
        Method[] methodList = obj.getClass().getMethods();
        Method m = null;
        for (Method method : methodList) {
            if (method.isAnnotationPresent(annotation)) {
                m = method;
                break;
            }
        }
        return m;
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
