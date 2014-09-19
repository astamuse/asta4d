package com.astamuse.asta4d.sample.handler.form;

import java.util.List;

import com.astamuse.asta4d.sample.util.persondb.JobExperence;
import com.astamuse.asta4d.sample.util.persondb.JobExperenceDbManager;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.flow.classical.MultiStepFormFlowHandler;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

public class CascadeEditHandler extends MultiStepFormFlowHandler<CascadeForm> {

    public CascadeEditHandler() {
        super(CascadeForm.class, "/templates/form/cascade/");
    }

    @RequestHandler
    public String handle(ExtraInfo extra) throws Exception {
        saveExtraDataToContext(extra);
        return super.handle();
    }

    @Override
    protected boolean treatCompleteStepAsExit() {
        return true;
    }

    @Override
    protected CascadeForm createInitForm() {
        ExtraInfo extra = getExtraDataFromContext();
        PersonForm pform = null;
        JobForm[] jforms = null;
        switch (extra.action) {
        case "add":
            pform = new PersonForm();
            jforms = new JobForm[0];
            break;
        case "edit":
            pform = PersonForm.buildFromPerson(PersonDbManager.instance().find(extra.id));
            List<JobExperence> jobs = JobExperenceDbManager.instance().find("personId", pform.getId());
            List<JobForm> jobFormList = ListConvertUtil.transform(jobs, new RowConvertor<JobExperence, JobForm>() {
                @Override
                public JobForm convert(int rowIndex, JobExperence job) {
                    return JobForm.buildFromJob(job);
                }
            });
            jforms = jobFormList.toArray(new JobForm[jobFormList.size()]);
            break;
        }
        pform.setAction(extra.action);

        CascadeForm cf = new CascadeForm();
        cf.setPersonForm(pform);
        cf.setJobForms(jforms);
        cf.setJobLength(jforms.length);

        return cf;
    }

    @Override
    protected void updateForm(CascadeForm form) {
        PersonForm pform = form.getPersonForm();
        JobForm[] jobs = form.getJobForms();
        switch (pform.getAction()) {
        case "add":
            PersonDbManager.instance().add(pform);

            for (JobForm job : jobs) {
                job.setPersonId(pform.getId());
                JobExperenceDbManager.instance().add(job);
            }

            DefaultMessageRenderingHelper.getConfiguredInstance().info("data inserted");
            break;
        case "edit":
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
            break;
        default:
            //
        }

    }

}
