package com.astamuse.asta4d.web.test.form;

import javax.validation.constraints.Max;

import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.Input;

@Form
public class SubArray2 {

    @Input(name = "age-@")
    @Max(100) Integer age;
}