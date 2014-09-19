package com.astamuse.asta4d.sample.handler.form;

import com.astamuse.asta4d.sample.MessageRenderingHelperFactory;
import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.flow.classical.MultiStepFormFlowHandler;

public class MultiStepEditHandler extends MultiStepFormFlowHandler<PersonForm> {

    public MultiStepEditHandler() {
        super(PersonForm.class, "/templates/form/multistep/");
    }

    @RequestHandler
    public String handle(ExtraInfo extra) throws Exception {
        saveExtraDataToContext(extra);
        return super.handle();
    }

    @Override
    protected boolean treatCompleteStepAsExit() {
        return true;
    }

    @Override
    protected PersonForm createInitForm() {
        ExtraInfo extra = getExtraDataFromContext();
        PersonForm form = null;
        switch (extra.action) {
        case "add":
            form = new PersonForm();
            break;
        case "edit":
            form = PersonForm.buildFromPerson(PersonDbManager.instance().find(extra.id));
            break;
        }
        form.setAction(extra.action);
        return form;
    }

    @Override
    protected void updateForm(PersonForm form) {
        switch (form.getAction()) {
        case "add":
            PersonDbManager.instance().add(Person.createByForm(form));
            MessageRenderingHelperFactory.getHelper().info("data inserted");
            break;
        case "edit":
            Person p = Person.createByForm(form);
            Person existingPerson = PersonDbManager.instance().find(form.getId());
            p.setId(existingPerson.getId());
            PersonDbManager.instance().update(p);
            MessageRenderingHelperFactory.getHelper().info("update succeed");
            break;
        default:
            //
        }

    }

}
