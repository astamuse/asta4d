package com.astamuse.asta4d.sample.handler.form;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.Checkbox;
import com.astamuse.asta4d.web.form.annotation.renderable.Hidden;
import com.astamuse.asta4d.web.form.annotation.renderable.Input;
import com.astamuse.asta4d.web.form.annotation.renderable.Radio;
import com.astamuse.asta4d.web.form.annotation.renderable.Select;
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

    @Hidden
    public String getAction() {
        return action;
    }

    @Override
    @Hidden
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
    @Select(name = "bloodtype")
    public BloodType getBloodType() {
        return super.getBloodType();
    }

    @Override
    @Radio
    public SEX getSex() {
        return super.getSex();
    }

    @Override
    @Checkbox
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
