package com.astamuse.asta4d.sample.handler.form;

import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.flow.classical.MultiStepFormFlowHandler;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

//@ShowCode:showMultiStepEditHandlerStart
public class MultiStepEditHandler extends MultiStepFormFlowHandler<PersonForm> {

    public MultiStepEditHandler(String templateBasePath) {
        super(PersonForm.class, templateBasePath);
    }

    @RequestHandler
    public String handle(Integer id) throws Exception {
        saveExtraDataToContext(id);
        return super.handle();
    }

    @Override
    protected boolean treatCompleteStepAsExit() {
        // change to true would cause the form flow exit immediately after the form data is updated
        // false would show a complete page after updated.
        return false;
    }

    @Override
    protected PersonForm createInitForm() {
        Integer id = getExtraDataFromContext();
        if (id == null) {
            return new PersonForm();
        } else {
            return PersonForm.buildFromPerson(PersonDbManager.instance().find(id));
        }
    }

    @Override
    protected void updateForm(PersonForm form) {
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
// @ShowCode:showMultiStepEditHandlerEnd