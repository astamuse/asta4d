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

import javax.validation.Valid;

import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.AvailableWhenEditOnly;
import com.astamuse.asta4d.web.form.annotation.renderable.Hidden;

//@ShowCode:showPeapleFormStart
@Form
public class PeopleForm {

    // a field with @CascadeFormField without arrayLengthField configured will be treated a simple reused form POJO
    @CascadeFormField
    @Valid
    // @NotEmpty
    private PersonForm mainPersonForm;

    @Hidden(name = "subperson-length")
    private Integer subpersonLength;

    // a field with @CascadeFormField with arrayLengthField configured will be treated an array field
    @CascadeFormField(name = "subperson", arrayLengthField = "subperson-length", containerSelector = "[cascade-ref=subperson-row-@]")
    @Valid
    private SubPersonForm[] personForms;

    // show the add and remove buttons only when edit mode

    @AvailableWhenEditOnly(selector = "#subperson-add-btn")
    private String subpersonAddBtn;

    @AvailableWhenEditOnly(selector = "#subperson-remove-btn")
    private String subpersonRemoveBtn;

    // @ShowCode:showPeapleFormEnd

    public PersonForm getMainPersonForm() {
        return mainPersonForm;
    }

    public void setMainPersonForm(PersonForm mainPersonForm) {
        this.mainPersonForm = mainPersonForm;
    }

    public Integer getSubpersonLength() {
        return subpersonLength;
    }

    public void setSubpersonLength(Integer subpersonLength) {
        this.subpersonLength = subpersonLength;
    }

    public SubPersonForm[] getPersonForms() {
        return personForms;
    }

    public void setPersonForms(SubPersonForm[] personForms) {
        this.personForms = personForms;
    }

}
