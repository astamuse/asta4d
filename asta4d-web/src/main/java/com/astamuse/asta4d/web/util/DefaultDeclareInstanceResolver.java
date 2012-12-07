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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDeclareInstanceResolver implements DeclareInstanceResolver {

    private final static Logger logger = LoggerFactory.getLogger(DefaultDeclareInstanceResolver.class);

    @SuppressWarnings("rawtypes")
    @Override
    public Object resolve(Object declaration) {
        try {
            if (declaration instanceof Class) {
                return ((Class) declaration).newInstance();
            } else if (declaration instanceof String) {
                Class<?> clz = Class.forName(declaration.toString());
                return clz.newInstance();
            } else {
                return declaration;
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            logger.warn("Can not create instance for:" + declaration.toString(), e);
            return null;
        }
    }

}
