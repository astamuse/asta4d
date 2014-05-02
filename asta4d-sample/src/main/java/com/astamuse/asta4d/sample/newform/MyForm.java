package com.astamuse.asta4d.sample.newform;

import javax.validation.constraints.Max;

import org.hibernate.validator.constraints.NotBlank;

import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.InputBox;
import com.astamuse.asta4d.web.form.annotation.renderable.SelectBox;

@Form
public class MyForm {

    public static enum BloodType {
        A, B, O, AB;
    }

    @InputBox
    @NotBlank
    private String name;

    @InputBox
    @Max(23)
    private Integer age;

    @SelectBox(name = "bloodtype")
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
