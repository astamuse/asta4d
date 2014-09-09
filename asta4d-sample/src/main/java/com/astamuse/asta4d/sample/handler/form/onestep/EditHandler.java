package com.astamuse.asta4d.sample.handler.form.onestep;

import com.astamuse.asta4d.sample.handler.form.PersonForm;
import com.astamuse.asta4d.web.form.flow.common.OneStepFormHandler;

public class EditHandler extends OneStepFormHandler<PersonForm> {

    public EditHandler() {
        super(PersonForm.class);
    }

}
