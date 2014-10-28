package com.astamuse.asta4d.sample.handler.form;

import com.astamuse.asta4d.sample.handler.form.common.CommonFormHandler;
import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

// @ShowCode:showOneStepEditHandlerStart
public class OneStepEditHandler extends CommonFormHandler<PersonForm> {

    public OneStepEditHandler(String inputTemplate) {
        super(PersonForm.class, inputTemplate);
    }

    @Override
    protected PersonForm createInitForm() throws Exception {
        PersonForm form = super.createInitForm();
        if (form.getId() == null) {// add
            return form;
        } else {// update
            // retrieve the form form db again
            return PersonForm.buildFromPerson(PersonDbManager.instance().find(form.getId()));
        }
    }

    @Override
    protected void updateForm(PersonForm form) {
        if (form.getId() == null) {// add
            PersonDbManager.instance().add(Person.createByForm(form));
            DefaultMessageRenderingHelper.getConfiguredInstance().info("data inserted");
        } else {// update
            Person p = Person.createByForm(form);
            PersonDbManager.instance().update(p);
            DefaultMessageRenderingHelper.getConfiguredInstance().info("update succeed");
        }
    }

}
// @ShowCode:showOneStepEditHandlerEnd