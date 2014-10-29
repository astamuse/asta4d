package com.astamuse.asta4d.sample.handler.form;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.AvailableWhenEditOnly;
import com.astamuse.asta4d.web.form.annotation.renderable.Hidden;

//@ShowCode:showCascadeFormStart
@Form
public class CascadeForm {

    // a field with @CascadeFormField without arrayLengthField configured will be treated a simple reused form POJO
    @CascadeFormField
    @Valid
    private PersonForm personForm;

    @Hidden(name = "job-experience-length")
    private Integer jobExperienceLength;

    // a field with @CascadeFormField with arrayLengthField configured will be treated an array field
    @CascadeFormField(name = "job-experience", arrayLengthField = "job-experience-length", containerSelector = "[cascade-ref=job-experience-row-@]")
    @Valid
    @NotEmpty
    private JobForm[] jobForms;

    // show the input comments only when edit mode

    @AvailableWhenEditOnly(selector = "#input-comment")
    private String inputComment;

    // show the add and remove buttons only when edit mode

    @AvailableWhenEditOnly(selector = "#job-experience-add-btn")
    private String jobExperienceAddBtn;

    @AvailableWhenEditOnly(selector = "#job-experience-remove-btn")
    private String jobExperienceRemoveBtn;

    // @ShowCode:showCascadeFormEnd

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
