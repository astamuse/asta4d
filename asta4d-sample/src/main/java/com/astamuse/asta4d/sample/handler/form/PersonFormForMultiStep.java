/*
 * Copyright 2014 astamuse company,Ltd.
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
package com.astamuse.asta4d.sample.handler.form;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.Input;
import com.astamuse.asta4d.web.form.annotation.renderable.Select;

@Form
public class PersonFormForMultiStep extends PersonForm {

    public static PersonFormForMultiStep buildFromPerson(Person p) {
        PersonFormForMultiStep form = new PersonFormForMultiStep();
        try {
            BeanUtils.copyProperties(form, p);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return form;
    }

    // @ShowCode:showAnnotatedMessageStart

    // afford an annotated message to override default generated message
    @Input(message = "validation.field.PersonForm.name")
    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    @Select(name = "bloodtype")
    public BloodType getBloodType() {
        return super.getBloodType();
    }

    // @ShowCode:showAnnotatedMessageEnd
}
