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
package com.astamuse.asta4d.web.form.flow.base;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.util.ClassUtil;

public class StepAwaredValidationFormHelper {

    /**
     * We use this cache as copyOnWrite, we do not mind the cache may fail in multiple thread, the content will be cached eventually at
     * sometime.
     */
    private static Map<String, Optional<Field>> ValidationTargetFieldCache = new HashMap<>();

    static final Object getValidationTargetByAnnotation(Object form, String step) {
        String cacheKey = step + "@" + form.getClass().getName();
        Optional<Field> oField = ValidationTargetFieldCache.get(cacheKey);

        if (oField == null) {
            List<Field> list = new ArrayList<>(ClassUtil.retrieveAllFieldsIncludeAllSuperClasses(form.getClass()));
            Iterator<Field> it = list.iterator();
            while (it.hasNext()) {
                Field f = it.next();
                StepAwaredValidationTarget vtAnno = f.getAnnotation(StepAwaredValidationTarget.class);
                if (vtAnno == null) {
                    continue;
                }
                String representingStep = vtAnno.value();
                if (step.equals(representingStep)) {
                    oField = Optional.of(f);
                    break;
                }
            }
            if (oField == null) {
                oField = Optional.empty();
            }
            if (Configuration.getConfiguration().isCacheEnable()) {
                Map<String, Optional<Field>> newCache = new HashMap<>(ValidationTargetFieldCache);
                newCache.put(cacheKey, oField);
                ValidationTargetFieldCache = newCache;
            }
        }

        if (oField.isPresent()) {
            try {
                return FieldUtils.readField(oField.get(), form, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            return form;
        }
    }

}
