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

import javax.validation.Valid;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.validator.constraints.NotEmpty;

import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.AvailableWhenEditOnly;
import com.astamuse.asta4d.web.form.annotation.renderable.Hidden;
import com.astamuse.asta4d.web.form.annotation.renderable.Input;

//@ShowCode:showPersonFormStart
//@Form to tell the framework this class can be initialized from context
//extend from the entity POJO to annotate form field definitions on getters.
@Form
public class SubPersonForm extends Person {

    public static SubPersonForm buildFromPerson(Person p) {
        SubPersonForm form = new SubPersonForm();
        try {
            BeanUtils.copyProperties(form, p);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return form;
    }

    public SubPersonForm() {
        subJobForms = new SubJobForm[0];
        subJobExperienceLength = subJobForms.length;
    }

    @Hidden(name = "sub-job-experience-length-@")
    private Integer subJobExperienceLength;

    // a field with @CascadeFormField with arrayLengthField configured will be treated an array field
    @CascadeFormField(name = "sub-job-experience-@", arrayLengthField = "sub-job-experience-length-@", containerSelector = "[cascade-ref=sub-job-experience-row-@-@@]")
    @Valid
    @NotEmpty
    private SubJobForm[] subJobForms;

    // show the add and remove buttons only when edit mode

    @AvailableWhenEditOnly(selector = "#sub-job-experience-add-btn-@")
    private String subJobExperienceAddBtn;

    @AvailableWhenEditOnly(selector = "#sub-job-experience-remove-btn-@")
    private String subJobExperienceRemoveBtn;

    @Override
    @Hidden(name = "subperson-id-@")
    public Integer getId() {
        return super.getId();
    }

    @Override
    @Input(name = "subperson-name-@")
    public String getName() {
        return super.getName();
    }

    @Override
    @Input(name = "subperson-age-@")
    public Integer getAge() {
        return super.getAge();
    }

    public Integer getSubJobExperienceLength() {
        return subJobExperienceLength;
    }

    public void setSubJobExperienceLength(Integer subJobExperienceLength) {
        this.subJobExperienceLength = subJobExperienceLength;
    }

    public SubJobForm[] getSubJobForms() {
        return subJobForms;
    }

    public void setSubJobForms(SubJobForm[] subJobForms) {
        this.subJobForms = subJobForms;
    }

}
// @ShowCode:showPersonFormEnd
