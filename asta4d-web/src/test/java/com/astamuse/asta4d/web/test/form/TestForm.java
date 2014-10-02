package com.astamuse.asta4d.web.test.form;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import com.astamuse.asta4d.web.form.annotation.CascadeFormField;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.Input;
import com.astamuse.asta4d.web.form.annotation.renderable.Hidden;

@Form
public class TestForm {

    @Hidden
    @Max(30) Integer id;

    @Input
    @NotBlank String data;

    @CascadeFormField
    @Valid SubForm subForm;

    @CascadeFormField(containerSelector = "#subArray-container", arrayLengthField = "subArrayLength")
    @NotEmpty
    @Valid SubArray[] subArray;

    @Hidden
    @NotNull Integer subArrayLength;

    @CascadeFormField(containerSelector = "#subArray2-container", arrayLengthField = "subArrayLength2")
    @NotEmpty
    @Valid SubArray2[] subArray2;

    @Hidden
    @NotNull Integer subArrayLength2;
}