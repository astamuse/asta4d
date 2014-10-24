package com.astamuse.asta4d.sample.handler.form;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.Hidden;

@Form
public class CascadeForm {

    @CascadeFormField
    @Valid
    private PersonForm personForm;

    @Hidden(name = "job-experience-length")
    private Integer jobExperienceLength;

    @CascadeFormField(name = "job-experience", arrayLengthField = "job-experience-length", containerSelector = "[cascade-ref=job-experience-row-@]")
    @Valid
    @NotEmpty
    private JobForm[] jobForms;

    public PersonForm getPersonForm() {
        return personForm;
    }

    public void setPersonForm(PersonForm personForm) {
        this.personForm = personForm;
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
