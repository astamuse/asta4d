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

import org.hibernate.validator.constraints.NotEmpty;

import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.AvailableWhenEditOnly;
import com.astamuse.asta4d.web.form.annotation.renderable.Hidden;
import com.astamuse.asta4d.web.form.flow.ext.MultiInputStepForm;

//@ShowCode:showSplittedFormStart
@Form
public class SplittedForm implements MultiInputStepForm {

    @Form
    public static class CascadeJobForm {

        // a field with @CascadeFormField with arrayLengthField configured will be treated an array field
        @CascadeFormField(name = "job-experience", arrayLengthField = "job-experience-length", containerSelector = "[cascade-ref=job-experience-row-@]")
        @Valid
        @NotEmpty
        private JobForm[] jobForms;

        @Hidden(name = "job-experience-length")
        private Integer jobExperienceLength;

        // show the add and remove buttons only when edit mode
        @AvailableWhenEditOnly(selector = "#job-experience-add-btn")
        private String jobExperienceAddBtn;

        @AvailableWhenEditOnly(selector = "#job-experience-remove-btn")
        private String jobExperienceRemoveBtn;

        public CascadeJobForm() {
            jobForms = new JobForm[0];
            jobExperienceLength = jobForms.length;
        }

        public Integer getJobExperienceLength() {
            return jobExperienceLength;
        }

        public void setJobExperienceLength(Integer jobExperienceLength) {
            this.jobExperienceLength = jobExperienceLength;
        }

        public JobForm[] getJobForms() {
            return jobForms;
        }

        public void setJobForms(JobForm[] jobForms) {
            this.jobForms = jobForms;
        }
    }

    // show the input comments only when edit mode
    @AvailableWhenEditOnly(selector = "#input-comment")
    private String inputComment;

    // a field with @CascadeFormField without arrayLengthField configured will be treated a simple reused form POJO
    @CascadeFormField
    @Valid
    private PersonForm personForm;

    @CascadeFormField
    @Valid
    private CascadeJobForm cascadeJobForm;

    public static final String inputStep2 = "input-2";

    public static final String inputStep1 = "input-1";

    public SplittedForm() {
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
    public Object getSubInputFormByStep(String step) {
        if (inputStep1.equalsIgnoreCase(step)) {
            return this.getPersonForm();
        } else if (inputStep2.equalsIgnoreCase(step)) {
            return this.getCascadeJobForm();
        } else {
            return null;
        }
    }

    @Override
    public void setSubInputFormForStep(String step, Object subForm) {
        if (inputStep1.equalsIgnoreCase(step)) {
            this.setPersonForm((PersonForm) subForm);
        } else if (inputStep2.equalsIgnoreCase(step)) {
            this.setCascadeJobForm((CascadeJobForm) subForm);
        } else {
            throw new IllegalArgumentException("Not recorgnized step:" + step);
        }
    }

}
// @ShowCode:showSplittedFormEnd

