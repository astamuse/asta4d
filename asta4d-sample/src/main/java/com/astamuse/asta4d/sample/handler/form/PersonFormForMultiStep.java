package com.astamuse.asta4d.sample.handler.form;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.web.form.annotation.Form;
import com.astamuse.asta4d.web.form.annotation.renderable.Input;

@Form
public class PersonFormForMultiStep extends PersonForm {

    public static PersonFormForMultiStep buildFromPerson(Person p) {
        PersonFormForMultiStep form = new PersonFormForMultiStep();
        try {
            BeanUtils.copyProperties(form, p);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return form;
    }

    // @ShowCode:showAnnotatedMessageStart

    // afford an annotated message to override default generated message
    @Input(message = "validation.field.PersonForm.name")
    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public BloodType getBloodType() {
        return super.getBloodType();
    }

    // @ShowCode:showAnnotatedMessageEnd
}
