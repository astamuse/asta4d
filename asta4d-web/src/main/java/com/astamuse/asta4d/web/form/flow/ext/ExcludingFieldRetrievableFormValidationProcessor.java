/*
 * Copyright 2016 astamuse company,Ltd.
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
package com.astamuse.asta4d.web.form.flow.ext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.astamuse.asta4d.web.form.flow.base.ValidationProcessor;
import com.astamuse.asta4d.web.form.validation.FormValidationMessage;

public interface ExcludingFieldRetrievableFormValidationProcessor extends ValidationProcessor {

    default List<FormValidationMessage> postValidate(Object form, List<FormValidationMessage> msgList) {
        return filterExcludedFieldsMessages(form, msgList);
    }

    /**
     * include/exclude corresponding field messages according to the {@link SimpleFormFieldIncludeDescription}/
     * {@link ExcludingFieldRetrievableForm}
     * 
     * @param form
     * @param msgList
     * @return
     */
    default List<FormValidationMessage> filterExcludedFieldsMessages(Object form, List<FormValidationMessage> msgList) {
        if (form instanceof ExcludingFieldRetrievableForm) {
            ExcludingFieldRetrievableForm vfe = (ExcludingFieldRetrievableForm) form;
            String[] fields = vfe.getExcludeFields();
            Set<String> set = new HashSet<>();
            for (String f : fields) {
                set.add(f);
            }
            return msgList.stream().filter(fvm -> !set.contains(fvm.getFieldName())).collect(Collectors.toList());
        } else {
            return msgList;
        }
    }
}
