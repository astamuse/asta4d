package com.astamuse.asta4d.sample.handler.form.multiinput;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

import com.astamuse.asta4d.sample.handler.form.JobForm;
import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.AvailableWhenEditOnly;
import com.astamuse.asta4d.web.form.annotation.renderable.Hidden;

@Form
public class CascadeJobForm {

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