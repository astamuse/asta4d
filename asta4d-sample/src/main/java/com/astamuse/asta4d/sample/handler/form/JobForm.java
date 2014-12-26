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

import com.astamuse.asta4d.sample.util.persondb.JobExperence;
import com.astamuse.asta4d.web.form.annotation.renderable.Hidden;
import com.astamuse.asta4d.web.form.annotation.renderable.Input;
import com.astamuse.asta4d.web.form.annotation.renderable.Select;

//@ShowCode:showJobFormStart
public class JobForm extends JobExperence {
    public static JobForm buildFromJob(JobExperence job) {
        JobForm form = new JobForm();
        try {
            BeanUtils.copyProperties(form, job);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return form;
    }

    // for arrayed form, all the field names must contain a "@" mark which will be rewritten to array index by framework.

    @Override
    @Hidden(name = "job-id-@")
    public Integer getId() {
        return super.getId();
    }

    @Override
    @Hidden(name = "job-person-id-@")
    public Integer getPersonId() {
        return super.getPersonId();
    }

    @Override
    @Select(name = "job-year-@")
    public Integer getYear() {
        return super.getYear();
    }

    @Override
    @Input(name = "job-description-@")
    public String getDescription() {
        return super.getDescription();
    }

}
// @ShowCode:showJobFormEnd
