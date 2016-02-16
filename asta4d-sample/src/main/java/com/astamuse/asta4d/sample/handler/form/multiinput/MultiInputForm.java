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
package com.astamuse.asta4d.sample.handler.form.multiinput;

import java.util.Map;
import java.util.Map.Entry;

import javax.validation.Valid;

import com.astamuse.asta4d.sample.handler.form.PersonForm;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.AvailableWhenEditOnly;
import com.astamuse.asta4d.web.form.flow.base.StepAwaredValidationTarget;
import com.astamuse.asta4d.web.form.flow.ext.MultiInputStepForm;

//@ShowCode:showSplittedFormStart
@Form
public class MultiInputForm implements MultiInputStepForm {

    // show the input comments only when edit mode
    @AvailableWhenEditOnly(selector = "#input-comment")
    private String inputComment;

    // a field with @CascadeFormField without arrayLengthField configured will be treated a simple reused form POJO
    @CascadeFormField
    @Valid
    @StepAwaredValidationTarget(inputStep1)
    private PersonForm personForm;

    @CascadeFormField
    @Valid
    @StepAwaredValidationTarget(inputStep2)
    private CascadeJobForm cascadeJobForm;

    public static final String inputStep1 = "input-1";

    public static final String inputStep2 = "input-2";

    public MultiInputForm() {
        personForm = new PersonForm();
        cascadeJobForm = new CascadeJobForm();
    }

    // getter/setter
    public PersonForm getPersonForm() {
        return personForm;
    }

    public void setPersonForm(PersonForm personForm) {
        this.personForm = personForm;
    }

    public CascadeJobForm getCascadeJobForm() {
        return cascadeJobForm;
    }

    public void setCascadeJobForm(CascadeJobForm cascadeJobForm) {
        this.cascadeJobForm = cascadeJobForm;
    }

    @Override
    public void mergeInputDataForConfirm(Map<String, Object> inputForms) {
        for (Entry<String, Object> entry : inputForms.entrySet()) {
            String step = entry.getKey();
            MultiInputForm form = (MultiInputForm) entry.getValue();
            switch (step) {
            case inputStep1:
                this.personForm = form.personForm;
                break;
            case inputStep2:
                this.cascadeJobForm = form.cascadeJobForm;
                break;
            default:
                //
            }
        }
    }

}
// @ShowCode:showSplittedFormEnd
