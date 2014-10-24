package com.astamuse.asta4d.sample.handler.form;

import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.flow.classical.OneStepFormHandler;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

// @ShowCode:showOneStepEditHandlerStart
public class OneStepEditHandler extends OneStepFormHandler<PersonForm> {

    public OneStepEditHandler(String inputTemplate) {
        super(PersonForm.class, inputTemplate);
    }

    @RequestHandler
    public String handle(Integer id) throws Exception {
        saveExtraDataToContext(id);
        return super.handle();
    }

    @Override
    protected PersonForm createInitForm() {
        Integer id = getExtraDataFromContext();
        if (id == null) {// add
            return new PersonForm();
        } else {// update
            return PersonForm.buildFromPerson(PersonDbManager.instance().find(id));
        }
    }

    @Override
    protected void updateForm(PersonForm form) {
        // ExtraInfo extra = getExtraDataFromContext();
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