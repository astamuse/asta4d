package com.astamuse.asta4d.sample.handler.form.onestep;

import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.sample.handler.form.PersonForm;
import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.web.annotation.QueryParam;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.form.flow.base.CommonFormResult;
import com.astamuse.asta4d.web.form.flow.classical.OneStepFormHandler;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

public class EditHandler extends OneStepFormHandler<PersonForm> {

    @ContextDataSet
    public static class ExtraInfo {
        @QueryParam
        String action;
        @QueryParam
        Integer id;
    }

    public EditHandler() {
        super(PersonForm.class);
    }

    @RequestHandler
    public CommonFormResult handle(ExtraInfo extra) throws Exception {
        saveExtraDataToContext(extra);
        return super.handleWithCommonFormResult();
    }

    @Override
    protected PersonForm createInitForm() {
        ExtraInfo extra = getExtraDataFromContext();
        switch (extra.action) {
        case "add":
            return new PersonForm();
        case "edit":
            return PersonDbManager.instance().find(extra.id);
        }
        return null;
    }

    @Override
    protected void updateForm(PersonForm form) {
        ExtraInfo extra = getExtraDataFromContext();
        switch (extra.action) {
        case "add":
            PersonDbManager.instance().add(Person.createByForm(form));
            DefaultMessageRenderingHelper.instance().info("data inserted");
            break;
        case "edit":
            Person p = Person.createByForm(form);
            Person existingPerson = PersonDbManager.instance().find(extra.id);
            p.setId(existingPerson.getId());
            PersonDbManager.instance().update(p);
            DefaultMessageRenderingHelper.instance().info("update succeed");
            break;
        default:
            //
        }

    }

}
