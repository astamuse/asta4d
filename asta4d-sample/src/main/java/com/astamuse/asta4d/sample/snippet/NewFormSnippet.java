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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.astamuse.asta4d.sample.newform.MyForm;
import com.astamuse.asta4d.sample.newform.MyForm.BloodType;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.field.FormFieldAdditionalRenderer;
import com.astamuse.asta4d.web.form.field.impl.SelectBoxAdditionalRenderer;

public class NewFormSnippet extends CommonFormSnippet {

    @Override
    protected List<FormFieldAdditionalRenderer> retrieveFieldAdditionalRenderer(String renderTargetStep, Object form) {
        List<FormFieldAdditionalRenderer> list = new LinkedList<>();
        list.add(new SelectBoxAdditionalRenderer(MyForm.class, "bloodtype").setOptionData(ListConvertUtil.transform(
                Arrays.asList(MyForm.BloodType.values()), new RowConvertor<MyForm.BloodType, Pair<String, String>>() {
                    @Override
                    public Pair<String, String> convert(int rowIndex, BloodType type) {
                        return Pair.of(type.name(), type.name());
                    }
                })));
        return list;
    }

}
