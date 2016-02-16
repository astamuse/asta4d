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
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;

import com.astamuse.asta4d.sample.handler.form.PersonForm;
import com.astamuse.asta4d.sample.handler.form.multiinput.CascadeJobForm;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.AvailableWhenEditOnly;
import com.astamuse.asta4d.web.form.flow.base.StepAwaredValidationTarget;
import com.astamuse.asta4d.web.form.flow.base.StepRepresentableForm;
import com.astamuse.asta4d.web.form.flow.classical.ClassicalFormFlowConstant;
import com.astamuse.asta4d.web.form.flow.ext.MultiInputStepForm;
import com.astamuse.asta4d.web.form.flow.ext.SimpleFormFieldExcludeDescprition;
import com.astamuse.asta4d.web.form.flow.ext.SimpleFormFieldExcludeHelper;

//@ShowCode:showSplittedFormStart
@Form
public class SplittedInputForm implements MultiInputStepForm, SimpleFormFieldExcludeHelper {

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
    public static class CascadeJobFormStep3 extends CascadeJobForm implements StepRepresentableForm {
        @Override
        public String[] retrieveRepresentingSteps() {
            return new String[] { "input-3" };
        }
    }

    @Form
    public static class ConfirmStepForm implements StepRepresentableForm, SimpleFormFieldExcludeHelper {

        @CascadeFormField
        private PersonForm personForm = new PersonForm();

        @CascadeFormField
        private CascadeJobForm cascadeJobForm = new CascadeJobForm();

        @Override
        public String[] retrieveRepresentingSteps() {
            return new String[] { ClassicalFormFlowConstant.STEP_CONFIRM, ClassicalFormFlowConstant.STEP_COMPLETE };
        }

        public PersonForm getPersonForm() {
            return personForm;
        }

        public CascadeJobForm getCascadeJobForm() {
            return cascadeJobForm;
        }

    }

    // private

    // show the input comments only when edit mode
    @AvailableWhenEditOnly(selector = "#input-comment")
    private String inputComment;

    // a field with @CascadeFormField without arrayLengthField configured will be treated a simple reused form POJO
    @CascadeFormField
    @StepAwaredValidationTarget(inputStep1)
    private PersonFormStep1 personFormStep1;

    @CascadeFormField
    @StepAwaredValidationTarget(inputStep2)
    private PersonFormStep2 personFormStep2;

    @CascadeFormField
    @StepAwaredValidationTarget(inputStep3)
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
    }

    // getter/setter

    public void setForms(PersonForm personForm, CascadeJobForm cascadeJobForm) {
        try {
            BeanUtils.copyProperties(this.personFormStep1, personForm);
            BeanUtils.copyProperties(this.personFormStep2, personForm);
            BeanUtils.copyProperties(this.cascadeJobFormStep3, cascadeJobForm);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public ConfirmStepForm getForms() {
        return this.confirmStepForm;
    }

    @Override
    public void mergeInputDataForConfirm(Map<String, Object> inputForms) {
        for (Entry<String, Object> entry : inputForms.entrySet()) {
            String step = entry.getKey();
            SplittedInputForm form = (SplittedInputForm) entry.getValue();
            switch (step) {
            case inputStep1:
                copyIncludeFieldsOnly(this.confirmStepForm.personForm, form.personFormStep1);
                break;
            case inputStep2:
                copyIncludeFieldsOnly(this.confirmStepForm.personForm, form.personFormStep2);
                break;
            case inputStep3:
                try {
                    BeanUtils.copyProperties(this.confirmStepForm.cascadeJobForm, form.cascadeJobFormStep3);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                //
            }
        }
    }

}
// @ShowCode:showSplittedFormEnd
