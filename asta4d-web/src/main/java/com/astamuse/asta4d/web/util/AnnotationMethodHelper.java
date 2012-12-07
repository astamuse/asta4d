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

package com.astamuse.asta4d.web.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.data.InjectUtil;

public class AnnotationMethodHelper {

    private final static Logger logger = LoggerFactory.getLogger(AnnotationMethodHelper.class);

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
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            String msg = "Error occured when invoke method for annotiona %s on %s with params:%s";
            msg = String.format(msg, annotation.getName(), targetObj.getClass().getName(), params);
            logger.error(msg, e);
            throw e;
        }
    }
}
