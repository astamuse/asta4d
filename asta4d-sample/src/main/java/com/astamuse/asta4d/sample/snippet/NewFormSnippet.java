/*
 * Copyright 2012 astamuse company,Ltd.
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
package com.astamuse.asta4d.sample.snippet;

import java.util.LinkedList;
import java.util.List;

import com.astamuse.asta4d.sample.newform.MyForm;
import com.astamuse.asta4d.sample.newform.MyForm.BloodType;
import com.astamuse.asta4d.sample.newform.MyForm.Language;
import com.astamuse.asta4d.sample.newform.MyForm.SEX;
import com.astamuse.asta4d.web.form.field.FormFieldAdditionalRenderer;
import com.astamuse.asta4d.web.form.field.impl.CheckBoxAdditionalRenderer;
import com.astamuse.asta4d.web.form.field.impl.RadioBoxAdditionalRenderer;
import com.astamuse.asta4d.web.form.field.impl.SelectBoxAdditionalRenderer;

public class NewFormSnippet extends CommonFormSnippet {
    @Override
    protected List<FormFieldAdditionalRenderer> retrieveFieldAdditionalRenderer(String renderTargetStep, Object form) {
        List<FormFieldAdditionalRenderer> list = new LinkedList<>();
        list.add(new SelectBoxAdditionalRenderer(MyForm.class, "bloodtype").setOptionData(BloodType.asOptionValueMap));
        list.add(new RadioBoxAdditionalRenderer(MyForm.class, "sex").setOptionData(SEX.asOptionValueMap));
        list.add(new CheckBoxAdditionalRenderer(MyForm.class, "language").setOptionData(Language.asOptionValueMap));
        return list;
    }
}
