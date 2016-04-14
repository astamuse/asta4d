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
package com.astamuse.asta4d.sample.handler.form.cascade;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.astamuse.asta4d.sample.util.persondb.Education;
import com.astamuse.asta4d.web.form.annotation.renderable.Hidden;
import com.astamuse.asta4d.web.form.annotation.renderable.Input;
import com.astamuse.asta4d.web.form.annotation.renderable.Select;

//@ShowCode:showEducationFormStart
public class EducationForm extends Education {
    public static EducationForm buildFromEducation(Education education) {
        EducationForm form = new EducationForm();
        try {
            BeanUtils.copyProperties(form, education);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return form;
    }

    // for arrayed form, all the field names must contain a "@" mark which will be rewritten to array index by framework.
    @Override
    @Hidden(name = "education-id-@")
    public Integer getId() {
        return super.getId();
    }

    @Override
    @Hidden(name = "education-person-id-@")
    public Integer getPersonId() {
        return super.getPersonId();
    }

    @Override
    @Select(name = "education-year-@")
    public Integer getYear() {
        return super.getYear();
    }

    @Override
    @Input(name = "education-description-@")
    public String getDescription() {
        return super.getDescription();
    }

}
// @ShowCode:showEducationFormEnd
