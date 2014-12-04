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
package com.astamuse.asta4d.web.form.annotation.renderable.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.astamuse.asta4d.util.annotation.AnnotationConvertor;
import com.astamuse.asta4d.web.form.annotation.FormField;
import com.astamuse.asta4d.web.form.field.FormFieldValueRenderer;

public class CommonInputAnnotationConvertor implements AnnotationConvertor<Annotation, FormField> {

    @Override
    public FormField convert(final Annotation originalAnnotation) {
        return new FormField() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return FormField.class;
            }

            @Override
            public String name() {
                return getValue(originalAnnotation, "name");
            }

            public String nameLabel() {
                return getValue(originalAnnotation, "nameLabel");
            }

            public String message() {
                return getValue(originalAnnotation, "message");
            }

            @Override
            public String editSelector() {
                return getValue(originalAnnotation, "editSelector");
            }

            @Override
            public String displaySelector() {
                return getValue(originalAnnotation, "displaySelector");
            }

            @Override
            public Class<? extends FormFieldValueRenderer> fieldValueRenderer() {
                return getValue(originalAnnotation, "fieldValueRenderer");
            }
        };

    }

    @SuppressWarnings("unchecked")
    private <T> T getValue(Annotation originalAnnotation, String name) {
        try {
            Method m = originalAnnotation.annotationType().getMethod(name);
            return (T) m.invoke(originalAnnotation);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
