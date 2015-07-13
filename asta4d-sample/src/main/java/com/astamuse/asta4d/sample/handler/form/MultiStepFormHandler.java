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

import com.astamuse.asta4d.sample.handler.form.common.Asta4DSamplePrjCommonFormHandler;
import com.astamuse.asta4d.sample.util.persondb.Person;
import com.astamuse.asta4d.sample.util.persondb.PersonDbManager;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

//@ShowCode:showMultiStepFormHandlerStart
public class MultiStepFormHandler extends Asta4DSamplePrjCommonFormHandler<PersonFormForMultiStep> {

    @Override
    public Class<PersonFormForMultiStep> getFormCls() {
        return PersonFormForMultiStep.class;
    }

    @Override
    public boolean treatCompleteStepAsExit() {
        // change to true would cause the form flow exit immediately after the form data is updated
        // false would show a complete page after updated.
        return false;
    }

    @Override
    public PersonFormForMultiStep createInitForm() throws Exception {
        PersonFormForMultiStep form = super.createInitForm();
        if (form.getId() == null) {// add
            return form;
        } else {// update
            // retrieve the form form db again
            return PersonFormForMultiStep.buildFromPerson(PersonDbManager.instance().find(form.getId()));
        }
    }

    @Override
    public void updateForm(PersonFormForMultiStep form) {
        if (form.getId() == null) {
            PersonDbManager.instance().add(Person.createByForm(form));
            // output the success message to specified DOM rather than the global message bar
            DefaultMessageRenderingHelper.getConfiguredInstance().info(".x-success-msg", "data inserted");
        } else {
            Person p = Person.createByForm(form);
            PersonDbManager.instance().update(p);
            // output the success message to specified DOM rather than the global message bar
            DefaultMessageRenderingHelper.getConfiguredInstance().info(".x-success-msg", "update succeed");
        }
    }

}
// @ShowCode:showMultiStepFormHandlerEnd