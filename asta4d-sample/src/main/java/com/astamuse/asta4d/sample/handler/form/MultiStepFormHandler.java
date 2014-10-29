package com.astamuse.asta4d.sample.handler.form;

import com.astamuse.asta4d.sample.handler.form.common.CommonFormHandler;
import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

//@ShowCode:showMultiStepFormHandlerStart
public class MultiStepFormHandler extends CommonFormHandler<PersonFormForMultiStep> {

    public MultiStepFormHandler(String templateBasePath) {
        super(PersonFormForMultiStep.class, templateBasePath);
    }

    @Override
    protected boolean treatCompleteStepAsExit() {
        // change to true would cause the form flow exit immediately after the form data is updated
        // false would show a complete page after updated.
        return false;
    }

    @Override
    protected PersonFormForMultiStep createInitForm() throws Exception {
        PersonFormForMultiStep form = super.createInitForm();
        if (form.getId() == null) {// add
            return form;
        } else {// update
            // retrieve the form form db again
            return PersonFormForMultiStep.buildFromPerson(PersonDbManager.instance().find(form.getId()));
        }
    }

    @Override
    protected void updateForm(PersonFormForMultiStep form) {
        if (form.getId() == null) {
            PersonDbManager.instance().add(Person.createByForm(form));
            DefaultMessageRenderingHelper.getConfiguredInstance().info("data inserted");
        } else {
            Person p = Person.createByForm(form);
            PersonDbManager.instance().update(p);
            DefaultMessageRenderingHelper.getConfiguredInstance().info("update succeed");
        }
    }

}
// @ShowCode:showMultiStepFormHandlerEnd