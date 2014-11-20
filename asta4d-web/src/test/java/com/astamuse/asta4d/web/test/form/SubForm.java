package com.astamuse.asta4d.web.test.form;

import javax.validation.constraints.NotNull;

import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.Input;

@Form
public class SubForm {
    @Input
    @NotNull Integer subData;
}