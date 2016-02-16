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

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyUtil;

public class ExcludingFieldRetrievableFormHelper {

    public static final void copyIncludeFieldsOnly(Object targetForm, ExcludingFieldRetrievableForm... froms) {
        try {
            List<AnnotatedPropertyInfo> toProps = AnnotatedPropertyUtil.retrieveProperties(targetForm.getClass());
            Map<String, AnnotatedPropertyInfo> toPropsMap = toProps.stream().collect(Collectors.toMap(p -> p.getName(), p -> p));

            for (ExcludingFieldRetrievableForm from : froms) {
                List<AnnotatedPropertyInfo> fromProps = AnnotatedPropertyUtil.retrieveProperties(from.getClass());
                String[] excludes = from.getExcludeFields();
                Set<String> set = new HashSet<>();
                for (String s : excludes) {
                    set.add(s);
                }
                for (AnnotatedPropertyInfo p : fromProps) {
                    // do not copy excluded fields
                    if (set.contains(p.getName())) {
                        continue;
                    }
                    toPropsMap.get(p.getName()).assignValue(targetForm, p.retrieveValue(from));
                }

            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}
