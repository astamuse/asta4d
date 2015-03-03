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
import com.astamuse.asta4d.web.form.flow.classical.MultiStepFormFlowHandler;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

//@ShowCode:showSplittedFormHandlerStart
public abstract class SplittedFormHandler extends MultiStepFormFlowHandler<SplittedForm> {

    public SplittedFormHandler() {
        super(SplittedForm.class);
    }

    protected String nextStepName() {
        return "next";
    }

    protected String storedConfirmStepName() {
        return "_" + confirmStepName();
    }

    protected boolean reachedConfirmStep(Map<String, Object> traceMap) {
        return traceMap.containsKey(storedConfirmStepName());
    }

    protected abstract boolean isEdit();

    @Override
    protected boolean treatCompleteStepAsExit() {
        // exit immediately after update without displaying complete page
        return true;
    }

    @Override
    protected boolean skipSaveTraceMap(String currentStep, String renderTargetStep, Map<String, Object> traceMap) {
        if (reachedConfirmStep(traceMap)) {
            return false;
        } else if (isEdit()) {
            return false;
        } else {
            return super.skipSaveTraceMap(currentStep, renderTargetStep, traceMap);
        }
    }

    @Override
    protected SplittedForm retrieveFormInstance(Map<String, Object> traceMap, String currentStep) {
        SplittedForm form = super.retrieveFormInstance(traceMap, currentStep);

        // first input
        if (isEdit() && currentStep.equalsIgnoreCase(FormFlowConstants.FORM_STEP_BEFORE_FIRST)) {
            traceMap.put(currentStep, form);
        }

        // input -> next
        if (currentStep.equalsIgnoreCase(firstStepName())) {
            SplittedForm storedForm = (SplittedForm) traceMap.get(storedConfirmStepName());
            if (storedForm == null) {
                storedForm = (SplittedForm) traceMap.get(FormFlowConstants.FORM_STEP_BEFORE_FIRST);
            }
            if (storedForm != null) {
                JobForm[] jobForms = storedForm.getJobForms();
                form.setJobForms(jobForms);
                form.setJobExperienceLength(jobForms.length);
                traceMap.remove(nextStepName());
            }
        }

        // next -> confirm
        // input <- next
        if (currentStep.equalsIgnoreCase(nextStepName())) {
            SplittedForm traceForm = (SplittedForm) traceMap.get(firstStepName());
            form.setPersonForm(traceForm.getPersonForm());
        }

        // next <- confirm
        if (currentStep.equalsIgnoreCase(confirmStepName())) {
            traceMap.put(storedConfirmStepName(), traceMap.get(confirmStepName()));
        }

        return form;
    }

    @Override
    protected SplittedForm generateFormInstanceFromContext() {
        SplittedForm form = super.generateFormInstanceFromContext();

        List<JobForm> rewriteJobList = new LinkedList<>();
        for (JobForm jform : form.getJobForms()) {
            List<JobPositionForm> rewritePosList = new LinkedList<>();
            for (JobPositionForm jpform : jform.getJobPositionForms()) {
                if (jpform.getJobId() != null) {
                    rewritePosList.add(jpform);
                }
            }
            jform.setJobPositionForms(rewritePosList.toArray(new JobPositionForm[rewritePosList.size()]));
            jform.setJobPositionLength(jform.getJobPositionForms().length);

            if (jform.getPersonId() != null) {
                rewriteJobList.add(jform);
            }
        }
        form.setJobForms(rewriteJobList.toArray(new JobForm[rewriteJobList.size()]));
        form.setJobExperienceLength(form.getJobForms().length);

        return form;
    }

    @Override
    protected CommonFormResult processValidation(FormProcessData processData, Object form) {
        String currentStep = processData.getStepCurrent();

        Object validateObj;

        if (currentStep.equalsIgnoreCase(firstStepName())) {
            validateObj = ((SplittedForm) form).getPersonForm();
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
        protected boolean isEdit() {
            return false;
        }

        @Override
        protected SplittedForm createInitForm() {
            SplittedForm form = new SplittedForm();

            return form;
        }

        @Override
        protected void updateForm(SplittedForm form) {
            PersonForm pForm = form.getPersonForm();
            PersonDbManager.instance().add(pForm);
            for (JobForm jForm : form.getJobForms()) {
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
        protected boolean isEdit() {
            return true;
        }

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

            SplittedForm form = new SplittedForm();
            form.setPersonForm(pForm);
            form.setJobForms(jForms);
            form.setJobExperienceLength(jForms.length);

            return form;
        }

        @Override
        protected void updateForm(SplittedForm form) {
            PersonForm pForm = form.getPersonForm();
            PersonDbManager.instance().update(pForm);

            List<Integer> validJobs = new LinkedList<Integer>();
            List<Integer> validJPs = new LinkedList<Integer>();
            for (JobForm jForm : form.getJobForms()) {
                jForm.setPersonId(pForm.getId());
                if (jForm.getId() > 0) {
                    JobExperenceDbManager.instance().update(jForm);
                } else {
                    JobExperenceDbManager.instance().add(jForm);
                }
                validJobs.add(jForm.getId());

                for (JobPositionForm jpForm : jForm.getJobPositionForms()) {
                    jpForm.setJobId(jForm.getId());
                    if (jpForm.getId() > 0) {
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
