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

import javax.validation.Valid;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.validator.constraints.NotEmpty;

import com.astamuse.asta4d.sample.handler.form.PersonForm;
import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.AvailableWhenEditOnly;
import com.astamuse.asta4d.web.form.annotation.renderable.Hidden;

@Form
public class PersonFormIncludingCascadeForm extends PersonForm {

    public static PersonFormIncludingCascadeForm buildFromPerson(Person p) {
        PersonFormIncludingCascadeForm form = new PersonFormIncludingCascadeForm();
        try {
            BeanUtils.copyProperties(form, p);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return form;
    }

    // @ShowCode:showPersonFormIncludingCascadeFormStart

    // show the input comments only when edit mode
    @AvailableWhenEditOnly(selector = "#input-comment")
    private String inputComment;

    // a field with @CascadeFormField with arrayLengthField configured will be treated an array field
    @CascadeFormField(name = "education", arrayLengthField = "education-length", containerSelector = "[cascade-ref=education-row-@]")
    @Valid
    @NotEmpty
    private EducationForm[] educationForms;

    @Hidden(name = "education-length")
    private Integer educationLength;

    // show the add and remove buttons only when edit mode
    @AvailableWhenEditOnly(selector = "#education-add-btn")
    private String educationAddBtn;

    @AvailableWhenEditOnly(selector = "#education-remove-btn")
    private String educationRemoveBtn;

    // @ShowCode:showPersonFormIncludingCascadeFormEnd

    // getter/setter
    public Integer getEducationLength() {
        return educationLength;
    }

    public void setEducationLength(Integer educationLength) {
        this.educationLength = educationLength;
    }

    public EducationForm[] getEducationForms() {
        return educationForms;
    }

    public void setEducationForms(EducationForm[] educationForms) {
        this.educationForms = educationForms;
    }
}
