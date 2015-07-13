/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.astamuse.asta4d.sample.handler.form;

import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.web.form.flow.classical.OneStepFormHandlerTrait;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

// @ShowCode:showSingleInputFormHandlerStart
public class SingleInputFormHandler implements OneStepFormHandlerTrait<PersonForm> {

    public Class<PersonForm> getFormCls() {
        return PersonForm.class;
    }

    @Override
    public PersonForm createInitForm() throws Exception {
        PersonForm form = OneStepFormHandlerTrait.super.createInitForm();
        if (form.getId() == null) {// add
            return form;
        } else {// update
            // retrieve the form form db again
            return PersonForm.buildFromPerson(PersonDbManager.instance().find(form.getId()));
        }
    }

    @Override
    public void updateForm(PersonForm form) {
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