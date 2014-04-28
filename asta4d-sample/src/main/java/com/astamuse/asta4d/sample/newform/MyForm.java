package com.astamuse.asta4d.sample.newform;

import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.InputBox;

@Form
public class MyForm {

    public static enum BloodType {
        A, B, O, AB;
    }

    @InputBox
    private String name;

    @InputBox
    private Integer age;

    @InputBox
    private BloodType bloodType = BloodType.AB;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

}
