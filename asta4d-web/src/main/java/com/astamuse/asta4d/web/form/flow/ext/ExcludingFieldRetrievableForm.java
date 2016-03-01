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

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;

public interface ExcludingFieldRetrievableForm {

    public String[] getExcludeFields();

    default void copyIncludingFieldsTo(Object targetForm) {
        ExcludingFieldRetrievableFormHelper.copyIncludeFieldsOnly(targetForm, this);
    }

    /**
     * Currently this is simple help method to simplify excluding declaration, thus we do not support cascaded form.We will add the full
     * support of cascade form in future.
     * 
     * @param including
     * @return
     */
    default String[] retrieveExcludeFieldsFromIncluding(String... including) {
        List<AnnotatedPropertyInfo> toProps = AnnotatedPropertyUtil.retrieveProperties(this.getClass());
        return toProps.stream().filter(p -> !ArrayUtils.contains(including, p.getName())).map(p -> p.getName()).toArray(size -> {
            return new String[size];
        });
    }
}
