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

package com.astamuse.asta4d.misc.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.astamuse.asta4d.web.util.bean.DeclareInstanceResolver;

public class SpringManagedInstanceResolver implements DeclareInstanceResolver, ApplicationContextAware {

    private ApplicationContext beanCtx;

    public SpringManagedInstanceResolver() {

    }

    public SpringManagedInstanceResolver(ApplicationContext beanCtx) {
        this.beanCtx = beanCtx;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanCtx = applicationContext;
    }

    @Override
    public Object resolve(Object declaration) {

        if (declaration instanceof Class) {
            Class<?> beanCls = (Class<?>) declaration;
            String[] names = beanCtx.getBeanNamesForType(beanCls);
            boolean beanExist = false;
            for (String name : names) {
                if (beanCtx.containsBean(name)) {
                    beanExist = true;
                    break;
                }
            }
            if (beanExist) {
                return new SpringManagedInstanceAdapter(beanCtx, beanCls, null);
            } else {
                return null;
            }
        } else if (declaration instanceof String) {
            String beanId = declaration.toString();
            if (beanCtx.containsBean(beanId)) {
                return new SpringManagedInstanceAdapter(beanCtx, null, beanId);
            } else {
                return null;
            }
        }
        return null;

    }

}
