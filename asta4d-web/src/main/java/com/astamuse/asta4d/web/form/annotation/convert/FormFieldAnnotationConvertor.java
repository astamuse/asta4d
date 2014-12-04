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
package com.astamuse.asta4d.web.form.annotation.convert;

import java.lang.annotation.Annotation;

import com.astamuse.asta4d.data.TypeUnMacthPolicy;
import com.astamuse.asta4d.util.annotation.AnnotationConvertor;
import com.astamuse.asta4d.web.annotation.QueryParam;
import com.astamuse.asta4d.web.form.annotation.FormField;

public class FormFieldAnnotationConvertor implements AnnotationConvertor<FormField, QueryParam> {

    @Override
    public QueryParam convert(final FormField originalAnnotation) {
        return new QueryParam() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return QueryParam.class;
            }

            @Override
            public TypeUnMacthPolicy typeUnMatch() {
                return TypeUnMacthPolicy.DEFAULT_VALUE_AND_TRACE;
            }

            @Override
            public String name() {
                return originalAnnotation.name();
            }
        };
    }

}
