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
package com.astamuse.asta4d.sample.handler.form.splittedinput;

import java.lang.reflect.InvocationTargetException;

import javax.validation.Valid;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.validator.constraints.NotEmpty;

import com.astamuse.asta4d.sample.handler.form.JobForm;
import com.astamuse.asta4d.sample.handler.form.PersonForm;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.AvailableWhenEditOnly;
import com.astamuse.asta4d.web.form.annotation.renderable.Hidden;
import com.astamuse.asta4d.web.form.flow.base.LifecycleAwaredForm;
import com.astamuse.asta4d.web.form.flow.base.StepRepresentableForm;
import com.astamuse.asta4d.web.form.flow.classical.ClassicalFormFlowConstant;
import com.astamuse.asta4d.web.form.flow.ext.MultiInputStepForm;
import com.astamuse.asta4d.web.form.flow.ext.SimpleFormFieldExcludeDescprition;
import com.astamuse.asta4d.web.form.flow.ext.SimpleFormFieldExcludeHelper;

//@ShowCode:showSplittedFormStart
@Form
public class SplittedInputForm implements MultiInputStepForm, LifecycleAwaredForm {

    @Form
    public static class PersonFormStep1 extends PersonForm implements SimpleFormFieldExcludeDescprition, StepRepresentableForm {

        @Override
        public String[] retrieveRepresentingSteps() {
            return new String[] { "input-1" };
        }

        @Override
        public String[] getExcludeFields() {
            return new String[] { "language", "memo" };
        }

    }

    @Form
    public static class PersonFormStep2 extends PersonForm implements SimpleFormFieldExcludeDescprition, StepRepresentableForm {

        @Override
        public String[] retrieveRepresentingSteps() {
            return new String[] { "input-2" };
        }

        @Override
        public String[] getExcludeFields() {
            return new String[] { "name", "age", "bloodtype", "sex" };
        }

    }

    @Form
    public static class CascadeJobFormStep3 {

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

        public CascadeJobFormStep3() {
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

    @Form
    public static class ConfirmStepForm implements StepRepresentableForm, SimpleFormFieldExcludeHelper {

        @CascadeFormField
        private PersonForm personForm = new PersonForm();

        @CascadeFormField
        private CascadeJobFormStep3 cascadeJobForm = new CascadeJobFormStep3();

        @Override
        public String[] retrieveRepresentingSteps() {
            return new String[] { ClassicalFormFlowConstant.STEP_CONFIRM, ClassicalFormFlowConstant.STEP_COMPLETE };
        }

        public void copydata(SplittedInputForm parentForm) {
            copyIncludeFieldsOnly(personForm, parentForm.personFormStep1, parentForm.personFormStep2);
            this.cascadeJobForm = parentForm.cascadeJobFormStep3;
        }

        public PersonForm getPersonForm() {
            return personForm;
        }

        public CascadeJobFormStep3 getCascadeJobForm() {
            return cascadeJobForm;
        }

    }

    // private

    // show the input comments only when edit mode
    @AvailableWhenEditOnly(selector = "#input-comment")
    private String inputComment;

    // a field with @CascadeFormField without arrayLengthField configured will be treated a simple reused form POJO
    @CascadeFormField
    private PersonFormStep1 personFormStep1;

    @CascadeFormField
    private PersonFormStep2 personFormStep2;

    @CascadeFormField
    private CascadeJobFormStep3 cascadeJobFormStep3;

    @CascadeFormField
    private ConfirmStepForm confirmStepForm;

    public static final String inputStep1 = "input-1";

    public static final String inputStep2 = "input-2";

    public static final String inputStep3 = "input-3";

    public SplittedInputForm() {
        this.personFormStep1 = new PersonFormStep1();
        this.personFormStep2 = new PersonFormStep2();
        this.cascadeJobFormStep3 = new CascadeJobFormStep3();
        this.confirmStepForm = new ConfirmStepForm();
        this.confirmStepForm.copydata(this);
    }

    @Override
    public void rewriteBeforeStored(String step) {
        // make sure all the instances of PersonFom holding all data
        if (step.equalsIgnoreCase(ClassicalFormFlowConstant.STEP_CONFIRM)) {
            this.confirmStepForm.copydata(this);
        }
    }

    // getter/setter

    public void setForms(PersonForm personForm, CascadeJobFormStep3 cascadeJobForm) {
        try {
            BeanUtils.copyProperties(this.personFormStep1, personForm);
            BeanUtils.copyProperties(this.personFormStep2, personForm);
            this.cascadeJobFormStep3 = cascadeJobForm;
            this.confirmStepForm.copydata(this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public ConfirmStepForm getForms() {
        return this.confirmStepForm;
    }

    @Override
    public Object getSubInputFormByStep(String step) {
        if (inputStep1.equalsIgnoreCase(step)) {
            return this.personFormStep1;
        } else if (inputStep2.equalsIgnoreCase(step)) {
            return this.personFormStep2;
        } else if (inputStep3.equalsIgnoreCase(step)) {
            return this.cascadeJobFormStep3;
        } else {
            return null;
        }
    }

    @Override
    public void setSubInputFormForStep(String step, Object subForm) {
        if (inputStep1.equalsIgnoreCase(step)) {
            personFormStep1 = (PersonFormStep1) subForm;
        } else if (inputStep2.equalsIgnoreCase(step)) {
            personFormStep2 = (PersonFormStep2) subForm;
        } else if (inputStep3.equalsIgnoreCase(step)) {
            this.cascadeJobFormStep3 = (CascadeJobFormStep3) subForm;
        } else {
            throw new IllegalArgumentException("Not recorgnized step:" + step);
        }
    }

    @Override
    public Object getValidationTarget(String step) {
        if (step.equalsIgnoreCase(ClassicalFormFlowConstant.STEP_COMPLETE)) {
            return this.confirmStepForm;
        } else {
            return MultiInputStepForm.super.getValidationTarget(step);
        }

    }

}
// @ShowCode:showSplittedFormEnd
