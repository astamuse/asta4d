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

//@ShowCode:showPersonFormStart
//@Form to tell the framework this class can be initialized from context
@Form
// extend from the entity POJO to annotate form field definitions on getters.
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

    @Override
    @Hidden
    public Integer getId() {
        return super.getId();
    }

    @Override
    // afford an annotated message to override default generated message
    @Input(message = "validation.field.PersonForm.name")
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

}
// @ShowCode:showPersonFormEnd
