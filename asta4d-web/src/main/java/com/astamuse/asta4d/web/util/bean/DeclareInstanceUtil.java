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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.web.WebApplicationConfiguration;

public class DeclareInstanceUtil {

    private final static DeclareInstanceResolver defaultResolver = new DefaultDeclareInstanceResolver();

    private final static Logger logger = LoggerFactory.getLogger(AnnotationMethodHelper.class);

    @SuppressWarnings("unchecked")
    public final static <T> T createInstance(Object declaration) {
        WebApplicationConfiguration conf = WebApplicationConfiguration.getWebApplicationConfiguration();
        List<DeclareInstanceResolver> resolverList = conf.getInstanceResolverList();
        Object handler = null;
        for (DeclareInstanceResolver resolver : resolverList) {
            handler = resolver.resolve(declaration);
            if (handler != null) {
                break;
            }
        }
        if (handler == null) {
            handler = defaultResolver.resolve(declaration);
        }
        return (T) handler;
    }

    public final static Object retrieveInovkeTargetObject(Object instance) {
        return instance instanceof DeclareInstanceAdapter ? ((DeclareInstanceAdapter) instance).asTargetInstance() : instance;
    }

    public final static Object invokeMethod(Object obj, Method m) throws Exception {

        Object[] params = InjectUtil.getMethodInjectParams(m);
        if (params == null) {
            params = new Object[0];
        }

        try {
            return m.invoke(obj, params);
        } catch (Exception e) {

            Exception throwEx = e;
            if (e instanceof InvocationTargetException) {
                Throwable t = ((InvocationTargetException) e).getTargetException();
                if (t instanceof Exception) {
                    throwEx = (Exception) t;
                }
            }

            String msg = "Error occured when invoke method for method:%s on %s, with params:%s";
            msg = String.format(msg, m.toString(), obj.getClass().getName(), params);
            logger.error(msg, throwEx);

            throw throwEx;
        }
    }
}
