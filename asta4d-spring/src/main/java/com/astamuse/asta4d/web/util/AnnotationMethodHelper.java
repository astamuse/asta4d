package com.astamuse.asta4d.web.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

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
}
