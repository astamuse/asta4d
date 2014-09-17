package com.astamuse.asta4d.sample.handler.form;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.CheckBox;
import com.astamuse.asta4d.web.form.annotation.renderable.Input;
import com.astamuse.asta4d.web.form.annotation.renderable.InputHidden;
import com.astamuse.asta4d.web.form.annotation.renderable.RadioBox;
import com.astamuse.asta4d.web.form.annotation.renderable.SelectBox;
import com.astamuse.asta4d.web.form.annotation.renderable.Textarea;

@Form
public class PersonForm extends Person {

    public static PersonForm buildFromPerson(Person p) {
        PersonForm form = new PersonForm();
        try {
            BeanUtils.copyProperties(form, p);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return form;
    }

    private String action;

    @InputHidden
    public String getAction() {
        return action;
    }

    @Override
    @InputHidden(name = "data-id")
    public Integer getId() {
        return super.getId();
    }

    @Override
    @Input
    public String getName() {
        return super.getName();
    }

    @Override
    @Input
    public Integer getAge() {
        return super.getAge();
    }

    @Override
    @SelectBox(name = "bloodtype")
    public BloodType getBloodType() {
        return super.getBloodType();
    }

    @Override
    @RadioBox
    public SEX getSex() {
        return super.getSex();
    }

    @Override
    @CheckBox
    public Language[] getLanguage() {
        return super.getLanguage();
    }

    @Override
    @Textarea
    public String getMemo() {
        return super.getMemo();
    }

    public void setAction(String action) {
        this.action = action;
    }

}
