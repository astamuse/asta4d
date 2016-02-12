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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.astamuse.asta4d.sample.util.persondb.Education;
import com.astamuse.asta4d.sample.util.persondb.EducationDbManager;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.flow.classical.ClassicalMultiStepFormFlowHandlerTrait;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

//@ShowCode:showCascadeFormHandlerStart
public abstract class CascadeFormHandler implements ClassicalMultiStepFormFlowHandlerTrait<PersonFormIncludingCascadeForm> {

    @Override
    public Class<PersonFormIncludingCascadeForm> getFormCls() {
        return PersonFormIncludingCascadeForm.class;
    }

    @Override
    public boolean treatCompleteStepAsExit() {
        // exit immediately after update without displaying complete page
        return true;
    }

    // intercept the form date construction to rewrite the form data
    @Override
    public PersonFormIncludingCascadeForm generateFormInstanceFromContext(String currentStep) {
        PersonFormIncludingCascadeForm form = ClassicalMultiStepFormFlowHandlerTrait.super.generateFormInstanceFromContext(currentStep);
        if (firstStepName().equals(currentStep)) {
            // for the submitting of input step, we need to rewrite the education list to handle deleted items.
            List<EducationForm> rewriteList = new LinkedList<>();
            for (EducationForm eform : form.getEducationForms()) {
                // we assume all the job forms without person id are removed by client
                if (eform.getPersonId() != null) {
                    rewriteList.add(eform);
                }
            }
            form.setEducationForms(rewriteList.toArray(new EducationForm[rewriteList.size()]));
        }
        return form;
    }

    /**
     * we do add logic by Add handler
     * 
     */
    public static class Add extends CascadeFormHandler {
        @Override
        public PersonFormIncludingCascadeForm createInitForm() {
            PersonFormIncludingCascadeForm form = new PersonFormIncludingCascadeForm();

            EducationForm[] eForms = new EducationForm[0];
            form.setEducationForms(eForms);
            form.setEducationLength(eForms.length);

            return form;
        }

        @Override
        public void updateForm(PersonFormIncludingCascadeForm form) {
            EducationForm[] eForms = form.getEducationForms();
            PersonDbManager.instance().add(form);
            for (EducationForm e : eForms) {
                e.setPersonId(form.getId());
                EducationDbManager.instance().add(e);
            }
            DefaultMessageRenderingHelper.getConfiguredInstance().info("data inserted");
        }
    }

    /**
     * we do update logic by Edit handler
     * 
     */
    public static class Edit extends CascadeFormHandler {

        @Override
        public PersonFormIncludingCascadeForm createInitForm() throws Exception {
            PersonFormIncludingCascadeForm superform = super.createInitForm();

            PersonFormIncludingCascadeForm form = PersonFormIncludingCascadeForm.buildFromPerson(PersonDbManager.instance().find(
                    superform.getId()));

            List<Education> educations = EducationDbManager.instance().find("personId", form.getId());
            List<EducationForm> eFormList = ListConvertUtil.transform(educations, new RowConvertor<Education, EducationForm>() {
                @Override
                public EducationForm convert(int rowIndex, Education e) {
                    return EducationForm.buildFromEducation(e);
                }
            });
            EducationForm[] eForms = eFormList.toArray(new EducationForm[eFormList.size()]);
            form.setEducationForms(eForms);
            form.setEducationLength(eForms.length);

            return form;
        }

        @Override
        public void updateForm(PersonFormIncludingCascadeForm form) {
            EducationForm[] eForms = form.getEducationForms();

            PersonDbManager.instance().update(form);

            List<Education> existingEdus = EducationDbManager.instance().find("personId", form.getId());

            Set<Integer> validIds = new HashSet<>();
            for (EducationForm edu : eForms) {
                edu.setPersonId(form.getId());
                if (edu.getId() == null) {
                    EducationDbManager.instance().add(edu);
                } else {
                    EducationDbManager.instance().update(edu);
                }
                validIds.add(edu.getId());
            }

            for (Education edu : existingEdus) {
                if (!validIds.contains(edu.getId())) {
                    EducationDbManager.instance().remove(edu);
                }
            }

            DefaultMessageRenderingHelper.getConfiguredInstance().info("update succeed");
        }
    }

}
// @ShowCode:showCascadeFormHandlerEnd
