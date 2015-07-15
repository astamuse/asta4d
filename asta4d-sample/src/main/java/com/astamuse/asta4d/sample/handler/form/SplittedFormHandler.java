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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.astamuse.asta4d.sample.handler.form.SplittedForm.CascadeJobForm;
import com.astamuse.asta4d.sample.handler.form.common.Asta4DSamplePrjCommonFormHandler;
import com.astamuse.asta4d.sample.util.persondb.JobExperence;
import com.astamuse.asta4d.sample.util.persondb.JobExperenceDbManager;
import com.astamuse.asta4d.sample.util.persondb.JobPosition;
import com.astamuse.asta4d.sample.util.persondb.JobPositionDbManager;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.flow.base.CommonFormResult;
import com.astamuse.asta4d.web.form.flow.base.FormFlowConstants;
import com.astamuse.asta4d.web.form.flow.base.FormFlowTraceData;
import com.astamuse.asta4d.web.form.flow.base.FormProcessData;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

//@ShowCode:showSplittedFormHandlerStart
public abstract class SplittedFormHandler extends Asta4DSamplePrjCommonFormHandler<SplittedForm> implements SplittedFormStepInfo {

    public Class<SplittedForm> getFormCls() {
        return SplittedForm.class;
    }

    @Override
    public String firstStepName() {
        return inputStep1Name();
    }

    @Override
    public boolean skipStoreTraceData(String currentStep, String renderTargetStep, FormFlowTraceData traceData) {
        // we will always save trace data when we start the flow because we need to retrieve the init form later.
        return completeStepName().equalsIgnoreCase(renderTargetStep);
    }

    @Override
    public void rewriteTraceDataBeforeGoSnippet(String currentStep, String renderTargetStep, FormFlowTraceData traceData) {
        Map<String, Object> formMap = traceData.getStepFormMap();
        if (inputStep1Name().equalsIgnoreCase(renderTargetStep) || inputStep2Name().equalsIgnoreCase(renderTargetStep)) {
            /*
             * we need to set the form data by the stored before first step data for each step when the form data is not set, 
             * otherwise we use the existing form data.
             * 
             * we forced to store the before first step data by return false at the method of skipStoreTraceData.
             */
            SplittedForm savedForm = (SplittedForm) formMap.get(renderTargetStep);
            if (savedForm == null) {
                SplittedForm initForm = (SplittedForm) formMap.get(FormFlowConstants.FORM_STEP_BEFORE_FIRST);
                formMap.put(renderTargetStep, initForm);
            }
        } else if (confirmStepName().equalsIgnoreCase(renderTargetStep)) {
            /* 
             * for the complete step, we should combine the saved data of first step and second step
             */
            SplittedForm form1 = (SplittedForm) formMap.get(inputStep1Name());
            SplittedForm form2 = (SplittedForm) formMap.get(inputStep2Name());

            // we can clone it or not, not matter
            SplittedForm confirmForm = (SplittedForm) form1;

            confirmForm.setCascadeJobForm(form2.getCascadeJobForm());

            formMap.put(confirmStepName(), confirmForm);
        } else {
            formMap.put(renderTargetStep, formMap.get(currentStep));
        }

    }

    @Override
    public SplittedForm generateFormInstanceFromContext(String currentStep) {
        SplittedForm form = super.generateFormInstanceFromContext(currentStep);

        if (inputStep2Name().equalsIgnoreCase(currentStep)) {
            // rewrite the array to handle deleted items
            CascadeJobForm cjForm = form.getCascadeJobForm();

            List<JobForm> rewriteJobList = new LinkedList<>();
            for (JobForm jobform : cjForm.getJobForms()) {
                List<JobPositionForm> rewritePosList = new LinkedList<>();
                // remove rows without job id specified
                for (JobPositionForm posForm : jobform.getJobPositionForms()) {
                    if (posForm.getJobId() != null) {
                        rewritePosList.add(posForm);
                    }
                }
                jobform.setJobPositionForms(rewritePosList.toArray(new JobPositionForm[rewritePosList.size()]));
                jobform.setJobPositionLength(jobform.getJobPositionForms().length);

                // remove rows without person id specified
                if (jobform.getPersonId() != null) {
                    rewriteJobList.add(jobform);
                }
            }
            cjForm.setJobForms(rewriteJobList.toArray(new JobForm[rewriteJobList.size()]));
            cjForm.setJobExperienceLength(cjForm.getJobForms().length);

        }

        return form;
    }

    @Override
    public CommonFormResult processValidation(FormProcessData processData, Object form) {
        String currentStep = processData.getStepCurrent();

        Object validateObj;

        // at the end of first input step, we will only validate the content of first step
        if (inputStep1Name().equalsIgnoreCase(currentStep)) {
            validateObj = ((SplittedForm) form).getPersonForm();
        } else if (inputStep2Name().equalsIgnoreCase(currentStep)) {
            validateObj = ((SplittedForm) form).getCascadeJobForm();
        } else {
            validateObj = form;
        }

        return super.processValidation(processData, validateObj);
    }

    /**
     * we do add logic by Add handler
     * 
     */
    public static class Add extends SplittedFormHandler {

        @Override
        public SplittedForm createInitForm() {
            SplittedForm form = new SplittedForm();

            return form;
        }

        @Override
        public void updateForm(SplittedForm form) {
            PersonForm pForm = form.getPersonForm();
            PersonDbManager.instance().add(pForm);
            for (JobForm jobForm : form.getCascadeJobForm().getJobForms()) {
                jobForm.setPersonId(pForm.getId());
                JobExperenceDbManager.instance().add(jobForm);
                for (JobPositionForm posForm : jobForm.getJobPositionForms()) {
                    posForm.setJobId(jobForm.getId());
                    JobPositionDbManager.instance().add(posForm);
                }
            }

            DefaultMessageRenderingHelper.getConfiguredInstance().info("data inserted");
        }
    }

    /**
     * we do update logic by Edit handler
     * 
     */
    public static class Edit extends SplittedFormHandler {

        @Override
        public SplittedForm createInitForm() throws Exception {
            SplittedForm superform = super.createInitForm();

            PersonForm pForm = PersonForm.buildFromPerson(PersonDbManager.instance().find(superform.getPersonForm().getId()));

            List<JobExperence> jobs = JobExperenceDbManager.instance().find("personId", pForm.getId());
            List<JobForm> jobFormList = ListConvertUtil.transform(jobs, new RowConvertor<JobExperence, JobForm>() {
                @Override
                public JobForm convert(int rowIndex, JobExperence j) {
                    return JobForm.buildFromJob(j);
                }
            });
            JobForm[] jobForms = jobFormList.toArray(new JobForm[jobFormList.size()]);

            for (JobForm jobform : jobForms) {
                List<JobPosition> posList = JobPositionDbManager.instance().find("jobId", jobform.getId());
                List<JobPositionForm> posFormList = ListConvertUtil.transform(posList, new RowConvertor<JobPosition, JobPositionForm>() {
                    @Override
                    public JobPositionForm convert(int rowIndex, JobPosition jp) {
                        return JobPositionForm.buildFromJobPosition(jp);
                    }
                });
                JobPositionForm[] posForms = posFormList.toArray(new JobPositionForm[posFormList.size()]);
                jobform.setJobPositionForms(posForms);
                jobform.setJobPositionLength(posForms.length);
            }

            CascadeJobForm cjForm = new CascadeJobForm();
            cjForm.setJobForms(jobForms);
            cjForm.setJobExperienceLength(jobForms.length);

            SplittedForm form = new SplittedForm();
            form.setPersonForm(pForm);
            form.setCascadeJobForm(cjForm);

            return form;
        }

        private final boolean isExistingId(Integer id) {
            if (id == null) {
                return false;
            } else {
                return id > 0;
            }
        }

        @Override
        public void updateForm(SplittedForm form) {
            PersonForm pForm = form.getPersonForm();
            PersonDbManager.instance().update(pForm);

            Set<Integer> validJobIds = new HashSet<>();
            Set<Integer> validPosIds = new HashSet<>();
            for (JobForm jobForm : form.getCascadeJobForm().getJobForms()) {
                jobForm.setPersonId(pForm.getId());
                if (isExistingId(jobForm.getId())) {
                    JobExperenceDbManager.instance().update(jobForm);
                } else {
                    JobExperenceDbManager.instance().add(jobForm);
                }
                validJobIds.add(jobForm.getId());

                for (JobPositionForm posForm : jobForm.getJobPositionForms()) {
                    posForm.setJobId(jobForm.getId());
                    if (isExistingId(posForm.getId())) {
                        JobPositionDbManager.instance().update(posForm);
                    } else {
                        JobPositionDbManager.instance().add(posForm);
                    }
                    validPosIds.add(posForm.getId());
                }
            }

            List<JobExperence> jobList = JobExperenceDbManager.instance().find("personId", pForm.getId());
            for (JobExperence job : jobList) {
                List<JobPosition> posList = JobPositionDbManager.instance().find("jobId", job.getId());
                for (JobPosition pos : posList) {
                    if (!validPosIds.contains(pos.getId())) {
                        JobPositionDbManager.instance().remove(pos);
                    }
                }
                if (!validJobIds.contains(job.getId())) {
                    JobExperenceDbManager.instance().remove(job);
                }
            }

            DefaultMessageRenderingHelper.getConfiguredInstance().info("update succeed");
        }

    }

}
// @ShowCode:showSplittedFormHandlerEnd
