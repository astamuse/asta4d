package com.astamuse.asta4d.sample.handler.form;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.InputHidden;

@Form
public class CascadeForm {

    @CascadeFormField
    @Valid
    private PersonForm personForm;

    @InputHidden(name = "job-length")
    private Integer jobLength;

    @CascadeFormField(name = "job", arrayLengthField = "job-length", containerSelector = "#job-experence-row")
    @Valid
    @NotEmpty
    private JobForm[] jobForms;

    public PersonForm getPersonForm() {
        return personForm;
    }

    public void setPersonForm(PersonForm personForm) {
        this.personForm = personForm;
    }

    public Integer getJobLength() {
        return jobLength;
    }

    public void setJobLength(Integer jobLength) {
        this.jobLength = jobLength;
    }

    public JobForm[] getJobForms() {
        return jobForms;
    }

    public void setJobForms(JobForm[] jobForms) {
        this.jobForms = jobForms;
    }

}
