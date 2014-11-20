package com.astamuse.asta4d.web.test.form;

import javax.validation.constraints.Max;

import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.Input;

@Form
public class SubArray {

    @Input(name = "year-@")
    @Max(2000) Integer year;
}