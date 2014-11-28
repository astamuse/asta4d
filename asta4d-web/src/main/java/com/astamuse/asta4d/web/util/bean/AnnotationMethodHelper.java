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

package com.astamuse.asta4d.web.util.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;

public class AnnotationMethodHelper {

    private final static Logger logger = LoggerFactory.getLogger(AnnotationMethodHelper.class);

    private final static ConcurrentHashMap<String, Method> methodCache = new ConcurrentHashMap<>();

    private final static String getCacheKey(Class<?> cls, Class<? extends Annotation> annotation) {
        return cls.getName() + "###annotation##" + annotation.getName();
    }

    public final static Method findMethod(Object obj, Class<? extends Annotation> annotation) {
        Method m = null;
        String cacheKey = getCacheKey(obj.getClass(), annotation);
        if (Configuration.getConfiguration().isCacheEnable()) {
            m = methodCache.get(cacheKey);
            if (m != null) {
                return m;
            }
        }

        m = findMethod(obj.getClass(), annotation);
        if (m != null) {
            m.setAccessible(true);
            methodCache.put(cacheKey, m);
        }
        return m;
    }

    private final static Method findMethod(Class<?> cls, Class<? extends Annotation> annotation) {
        if (cls == null || cls.getName().equals(Object.class.getName())) {
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
            m = findMethod(cls.getSuperclass(), annotation);
            if (m == null) {
                Class<?>[] intfs = cls.getInterfaces();
                for (Class<?> intf : intfs) {
                    m = findMethod(intf, annotation);
                    if (m != null) {
                        break;
                    }
                }
                /*
                 * we need to find out the implemented method rather than the interface declared method because 
                 * we need retrieve the actual parameter names later.
                 */
                if (m != null) {
                    try {
                        m = cls.getMethod(m.getName(), m.getParameterTypes());
                    } catch (Exception e) {
                        // it seems impossible
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return m;
    }

}
