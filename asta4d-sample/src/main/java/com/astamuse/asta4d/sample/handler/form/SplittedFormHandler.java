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
import java.util.Map;

import com.astamuse.asta4d.sample.handler.form.SplittedForm.CascadeJobForm;
import com.astamuse.asta4d.sample.util.persondb.JobExperence;
import com.astamuse.asta4d.sample.util.persondb.JobExperenceDbManager;
import com.astamuse.asta4d.sample.util.persondb.JobPosition;
import com.astamuse.asta4d.sample.util.persondb.JobPositionDbManager;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.flow.base.CommonFormResult;
import com.astamuse.asta4d.web.form.flow.base.FormFlowConstants;
import com.astamuse.asta4d.web.form.flow.base.FormProcessData;
import com.astamuse.asta4d.web.form.flow.classical.ClassicalFormFlowConstant;
import com.astamuse.asta4d.web.form.flow.classical.MultiStepFormFlowHandler;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

//@ShowCode:showSplittedFormHandlerStart
public abstract class SplittedFormHandler extends MultiStepFormFlowHandler<SplittedForm> implements SplittedFormStepInfo {

    public SplittedFormHandler() {
        super(SplittedForm.class);
    }

    @Override
    protected String firstStepName() {
        return inputStep1Name();
    }

    @Override
    protected boolean treatCompleteStepAsExit() {
        // exit immediately after update without displaying complete page
        return true;
    }

    @Override
    protected boolean skipSaveTraceMap(String currentStep, String renderTargetStep, Map<String, Object> traceMap) {
        // we will always save trace map when we go to the first step because we need to retrieve the init form later.
        if (inputStep1Name().equalsIgnoreCase(renderTargetStep)) {
            return false;
        } else {
            return super.skipSaveTraceMap(currentStep, renderTargetStep, traceMap);
        }
    }

    @Override
    protected void passDataToSnippet(String currentStep, String renderTargetStep, Map<String, Object> traceMap) {
        if (inputStep2Name().equalsIgnoreCase(renderTargetStep)) {
            /* 
             * for the first input step, the initial form will be used by default, but for the second input step, we must store the initial 
             * data by ourselves, which is why we force to save trace map data when we go to the first step(from the before first step at 
             * when we retrieved the initial form from db)
             */
            SplittedForm savedForm = (SplittedForm) traceMap.get(renderTargetStep);
            if (savedForm == null) {
                SplittedForm initForm = (SplittedForm) traceMap.get(FormFlowConstants.FORM_STEP_BEFORE_FIRST);
                traceMap.put(renderTargetStep, initForm);
            }
        } else if (confirmStepName().equalsIgnoreCase(renderTargetStep)) {
            /* 
             * for the complete step, we should combine the saved data of first step and second step
             */
            SplittedForm form1 = (SplittedForm) traceMap.get(inputStep1Name());
            SplittedForm form2 = (SplittedForm) traceMap.get(inputStep2Name());

            // we can clone it or not, not matter
            SplittedForm confirmForm = (SplittedForm) form1;

            confirmForm.setCascadeJobForm(form2.getCascadeJobForm());

            traceMap.put(ClassicalFormFlowConstant.STEP_CONFIRM, confirmForm);
        }

        super.passDataToSnippet(currentStep, renderTargetStep, traceMap);
    }

    @Override
    protected SplittedForm generateFormInstanceFromContext(String currentStep) {
        SplittedForm form = super.generateFormInstanceFromContext(currentStep);

        if (inputStep2Name().equalsIgnoreCase(currentStep)) {
            // rewrite the array to handle deleted items
            CascadeJobForm cjForm = form.getCascadeJobForm();

            List<JobForm> rewriteJobList = new LinkedList<>();
            for (JobForm jform : cjForm.getJobForms()) {
                List<JobPositionForm> rewritePosList = new LinkedList<>();
                // remove rows without job id specified
                for (JobPositionForm jpform : jform.getJobPositionForms()) {
                    if (jpform.getJobId() != null) {
                        rewritePosList.add(jpform);
                    }
                }
                jform.setJobPositionForms(rewritePosList.toArray(new JobPositionForm[rewritePosList.size()]));
                jform.setJobPositionLength(jform.getJobPositionForms().length);

                // remove rows without person id specified
                if (jform.getPersonId() != null) {
                    rewriteJobList.add(jform);
                }
            }
            cjForm.setJobForms(rewriteJobList.toArray(new JobForm[rewriteJobList.size()]));
            cjForm.setJobExperienceLength(cjForm.getJobForms().length);

        }

        return form;
    }

    @Override
    protected CommonFormResult processValidation(FormProcessData processData, Object form) {
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
        protected SplittedForm createInitForm() {
            SplittedForm form = new SplittedForm();

            return form;
        }

        @Override
        protected void updateForm(SplittedForm form) {
            PersonForm pForm = form.getPersonForm();
            PersonDbManager.instance().add(pForm);
            for (JobForm jForm : form.getCascadeJobForm().getJobForms()) {
                jForm.setPersonId(pForm.getId());
                JobExperenceDbManager.instance().add(jForm);
                for (JobPositionForm jpForm : jForm.getJobPositionForms()) {
                    jpForm.setJobId(jForm.getId());
                    JobPositionDbManager.instance().add(jpForm);
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
        protected SplittedForm createInitForm() throws Exception {
            SplittedForm superform = super.createInitForm();

            PersonForm pForm = PersonForm.buildFromPerson(PersonDbManager.instance().find(superform.getPersonForm().getId()));

            List<JobExperence> jobs = JobExperenceDbManager.instance().find("personId", pForm.getId());
            List<JobForm> jFormList = ListConvertUtil.transform(jobs, new RowConvertor<JobExperence, JobForm>() {
                @Override
                public JobForm convert(int rowIndex, JobExperence j) {
                    return JobForm.buildFromJob(j);
                }
            });
            JobForm[] jForms = jFormList.toArray(new JobForm[jFormList.size()]);

            for (JobForm jform : jForms) {
                List<JobPosition> jps = JobPositionDbManager.instance().find("jobId", jform.getId());
                List<JobPositionForm> jpFormList = ListConvertUtil.transform(jps, new RowConvertor<JobPosition, JobPositionForm>() {
                    @Override
                    public JobPositionForm convert(int rowIndex, JobPosition jp) {
                        return JobPositionForm.buildFromJobPosition(jp);
                    }
                });
                JobPositionForm[] jpForms = jpFormList.toArray(new JobPositionForm[jpFormList.size()]);
                jform.setJobPositionForms(jpForms);
                jform.setJobPositionLength(jpForms.length);
            }

            CascadeJobForm cjForm = new CascadeJobForm();
            cjForm.setJobForms(jForms);
            cjForm.setJobExperienceLength(jForms.length);

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
        protected void updateForm(SplittedForm form) {
            PersonForm pForm = form.getPersonForm();
            PersonDbManager.instance().update(pForm);

            List<Integer> validJobs = new LinkedList<Integer>();
            List<Integer> validJPs = new LinkedList<Integer>();
            for (JobForm jForm : form.getCascadeJobForm().getJobForms()) {
                jForm.setPersonId(pForm.getId());
                if (isExistingId(jForm.getId())) {
                    JobExperenceDbManager.instance().update(jForm);
                } else {
                    JobExperenceDbManager.instance().add(jForm);
                }
                validJobs.add(jForm.getId());

                for (JobPositionForm jpForm : jForm.getJobPositionForms()) {
                    jpForm.setJobId(jForm.getId());
                    if (isExistingId(jpForm.getId())) {
                        JobPositionDbManager.instance().update(jpForm);
                    } else {
                        JobPositionDbManager.instance().add(jpForm);
                    }
                    validJPs.add(jpForm.getId());
                }
            }

            List<JobExperence> jobs = JobExperenceDbManager.instance().find("personId", pForm.getId());
            for (JobExperence job : jobs) {
                List<JobPosition> jps = JobPositionDbManager.instance().find("jobId", job.getId());
                for (JobPosition jp : jps) {
                    if (!validJPs.contains(jp.getId())) {
                        JobPositionDbManager.instance().remove(jp);
                    }
                }
                if (!validJobs.contains(job.getId())) {
                    JobExperenceDbManager.instance().remove(job);
                }
            }

            DefaultMessageRenderingHelper.getConfiguredInstance().info("update succeed");
        }

    }

}
// @ShowCode:showSplittedFormHandlerEnd
