package com.astamuse.asta4d.sample.handler.form;

import java.util.LinkedList;
import java.util.List;

import com.astamuse.asta4d.sample.util.persondb.JobExperence;
import com.astamuse.asta4d.sample.util.persondb.JobExperenceDbManager;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.flow.classical.MultiStepFormFlowHandler;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

public abstract class CascadeEditHandler extends MultiStepFormFlowHandler<CascadeForm> {

    public CascadeEditHandler() {
        super(CascadeForm.class);
    }

    @Override
    protected boolean treatCompleteStepAsExit() {
        return true;
    }

    @Override
    protected CascadeForm generateFormInstanceFromContext() {
        CascadeForm form = super.generateFormInstanceFromContext();
        List<JobForm> rewriteList = new LinkedList<>();
        for (JobForm jform : form.getJobForms()) {
            if (jform.getPersonId() != null) {
                rewriteList.add(jform);
            }
        }
        form.setJobForms(rewriteList.toArray(new JobForm[rewriteList.size()]));
        return form;
    }

    public static class Add extends CascadeEditHandler {
        @Override
        protected CascadeForm createInitForm() {
            PersonForm pform = new PersonForm();
            JobForm[] jforms = new JobForm[0];

            CascadeForm cf = new CascadeForm();
            cf.setPersonForm(pform);
            cf.setJobForms(jforms);
            cf.setJobExperienceLength(jforms.length);

            return cf;
        }

        @Override
        protected void updateForm(CascadeForm form) {
            PersonForm pform = form.getPersonForm();
            JobForm[] jobs = form.getJobForms();
            PersonDbManager.instance().add(pform);
            for (JobForm job : jobs) {
                job.setPersonId(pform.getId());
                JobExperenceDbManager.instance().add(job);
            }
            DefaultMessageRenderingHelper.getConfiguredInstance().info("data inserted");
        }
    }

    public static class Edit extends CascadeEditHandler {

        @RequestHandler
        public String handle(Integer id) throws Exception {
            saveExtraDataToContext(id);
            return super.handle();
        }

        @Override
        protected CascadeForm createInitForm() {
            Integer id = getExtraDataFromContext();

            PersonForm pform = PersonForm.buildFromPerson(PersonDbManager.instance().find(id));
            List<JobExperence> jobs = JobExperenceDbManager.instance().find("personId", pform.getId());
            List<JobForm> jobFormList = ListConvertUtil.transform(jobs, new RowConvertor<JobExperence, JobForm>() {
                @Override
                public JobForm convert(int rowIndex, JobExperence job) {
                    return JobForm.buildFromJob(job);
                }
            });
            JobForm[] jforms = jobFormList.toArray(new JobForm[jobFormList.size()]);

            CascadeForm cf = new CascadeForm();
            cf.setPersonForm(pform);
            cf.setJobForms(jforms);
            cf.setJobExperienceLength(jforms.length);

            return cf;
        }

        @Override
        protected void updateForm(CascadeForm form) {
            PersonForm pform = form.getPersonForm();
            JobForm[] jobs = form.getJobForms();

            PersonDbManager.instance().update(pform);
            for (JobForm job : jobs) {
                job.setPersonId(pform.getId());
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
