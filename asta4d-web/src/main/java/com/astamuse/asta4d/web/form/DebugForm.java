package com.astamuse.asta4d.web.form;

import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.InputBox;

@Form
public class DebugForm {

    @InputBox
    private String name;

    @InputBox
    private int age;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

}
