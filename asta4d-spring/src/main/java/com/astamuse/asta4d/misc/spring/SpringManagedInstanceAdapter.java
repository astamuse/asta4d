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

import org.springframework.context.ApplicationContext;

import com.astamuse.asta4d.web.util.bean.DeclareInstanceAdapter;

public class SpringManagedInstanceAdapter implements DeclareInstanceAdapter {

    private ApplicationContext beanCtx;

    private Class<?> beanCls = null;

    private String beanId = null;

    public SpringManagedInstanceAdapter(ApplicationContext beanCtx, Class<?> beanCls, String beanId) {
        this.beanCtx = beanCtx;
        this.beanCls = beanCls;
        this.beanId = beanId;
    }

    @Override
    public Object asTargetInstance() {
        if (beanCls != null) {
            return beanCtx.getBean(beanCls);
        } else if (beanId != null) {
            return beanCtx.getBean(beanId);
        } else {
            return null;
        }
    }

}
