/*
 * Copyright 2016 astamuse company,Ltd.
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
package com.astamuse.asta4d.web.form.flow.base;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Use this interface to indicate the representing input step when a single form class is to be used in different input steps.
 * 
 * NOTE: This interface only works at rendering stage even the name suggests it may work at validation stage. This confusion will be fixed
 * in future.
 * 
 * @author e-ryu
 *
 */
public interface StepRepresentableForm {

    default void copyPropertiesFrom(Object from) {
        try {
            BeanUtils.copyProperties(this, from);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    default void copyPropertiesTo(Object target) {
        try {
            BeanUtils.copyProperties(target, this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] retrieveRepresentingSteps();
}
