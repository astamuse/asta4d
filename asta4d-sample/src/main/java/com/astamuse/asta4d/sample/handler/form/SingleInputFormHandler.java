package com.astamuse.asta4d.sample.handler.form;

import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.web.form.flow.classical.OneStepFormHandler;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

// @ShowCode:showSingleInputFormHandlerStart
public class SingleInputFormHandler extends OneStepFormHandler<PersonForm> {

    public SingleInputFormHandler(String inputTemplate) {
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
            // the success message will be shown at the default global message bar
            DefaultMessageRenderingHelper.getConfiguredInstance().info("data inserted");
        } else {// update
            Person p = Person.createByForm(form);
            PersonDbManager.instance().update(p);
            // the success message will be shown at the default global message bar
            DefaultMessageRenderingHelper.getConfiguredInstance().info("update succeed");
        }
    }

}
// @ShowCode:showSingleInputFormHandlerEnd