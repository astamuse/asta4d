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

import java.util.LinkedList;
import java.util.List;

import com.astamuse.asta4d.sample.util.persondb.JobExperence;
import com.astamuse.asta4d.sample.util.persondb.JobExperenceDbManager;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.flow.classical.MultiStepFormFlowHandler;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

//@ShowCode:showCascadeFormHandlerStart
public abstract class CascadeFormHandler extends MultiStepFormFlowHandler<CascadeForm> {

    public CascadeFormHandler() {
        super(CascadeForm.class);
    }

    @Override
    protected boolean treatCompleteStepAsExit() {
        // exit immediately after update without displaying complete page
        return true;
    }

    // intercept the form date construction to rewrite the form data
    @Override
    protected CascadeForm generateFormInstanceFromContext() {
        CascadeForm form = super.generateFormInstanceFromContext();
        List<JobForm> rewriteList = new LinkedList<>();
        for (JobForm jform : form.getJobForms()) {
            // we assume all the job forms without person id are removed by client
            if (jform.getPersonId() != null) {
                rewriteList.add(jform);
            }
        }
        form.setJobForms(rewriteList.toArray(new JobForm[rewriteList.size()]));
        return form;
    }

    /**
     * we do add logic by Add handler
     * 
     */
    public static class Add extends CascadeFormHandler {
        @Override
        protected CascadeForm createInitForm() {
            PeopleForm pform = new PeopleForm();
            PersonForm mpform = new PersonForm();
            SubPersonForm[] pforms = new SubPersonForm[0];
            JobForm[] jforms = new JobForm[0];

            CascadeForm cf = new CascadeForm();
            pform.setMainPersonForm(mpform);
            pform.setPersonForms(pforms);
            pform.setSubpersonLength(pforms.length);
            cf.setPeopleForm(pform);
            cf.setJobForms(jforms);
            cf.setJobExperienceLength(jforms.length);

            return cf;
        }

        @Override
        protected void updateForm(CascadeForm form) {
            PeopleForm pform = form.getPeopleForm();
            PersonForm mperson = pform.getMainPersonForm();
            SubPersonForm[] people = pform.getPersonForms();
            JobForm[] jobs = form.getJobForms();
            PersonDbManager.instance().add(mperson);
            for (SubPersonForm person : people) {
                PersonDbManager.instance().add(person);
            }
            for (JobForm job : jobs) {
                job.setPersonId(mperson.getId());
                JobExperenceDbManager.instance().add(job);
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
        protected CascadeForm createInitForm() throws Exception {
            CascadeForm superForm = super.createInitForm();

            PersonForm pform = PersonForm.buildFromPerson(PersonDbManager.instance().find(
                    superForm.getPeopleForm().getMainPersonForm().getId()));
            List<JobExperence> jobs = JobExperenceDbManager.instance().find("personId", pform.getId());
            List<JobForm> jobFormList = ListConvertUtil.transform(jobs, new RowConvertor<JobExperence, JobForm>() {
                @Override
                public JobForm convert(int rowIndex, JobExperence job) {
                    return JobForm.buildFromJob(job);
                }
            });
            JobForm[] jforms = jobFormList.toArray(new JobForm[jobFormList.size()]);

            CascadeForm cf = new CascadeForm();
            PeopleForm peopleForm = new PeopleForm();
            peopleForm.setMainPersonForm(pform);
            SubPersonForm[] pforms = new SubPersonForm[0];
            peopleForm.setPersonForms(pforms);
            peopleForm.setSubpersonLength(pforms.length);
            cf.setPeopleForm(peopleForm);
            cf.setJobForms(jforms);
            cf.setJobExperienceLength(jforms.length);

            return cf;
        }

        @Override
        protected void updateForm(CascadeForm form) {
            PeopleForm pform = form.getPeopleForm();
            PersonForm mp = pform.getMainPersonForm();
            SubPersonForm[] people = pform.getPersonForms();
            JobForm[] jobs = form.getJobForms();

            PersonDbManager.instance().update(mp);

            for (SubPersonForm person : people) {
                PersonDbManager.instance().add(person);
            }
            for (JobForm job : jobs) {
                job.setPersonId(mp.getId());
                if (job.getId() == null) {
                    JobExperenceDbManager.instance().add(job);
                } else {
                    JobExperenceDbManager.instance().update(job);
                }
            }
            DefaultMessageRenderingHelper.getConfiguredInstance().info("update succeed");
        }
    }

}
// @ShowCode:showCascadeFormHandlerEnd
