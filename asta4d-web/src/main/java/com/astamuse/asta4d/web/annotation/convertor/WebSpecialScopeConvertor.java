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
package com.astamuse.asta4d.web.annotation.convertor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.astamuse.asta4d.data.TypeUnMacthPolicy;
import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.util.annotation.AnnotationConvertor;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.annotation.CookieData;
import com.astamuse.asta4d.web.annotation.FlashData;
import com.astamuse.asta4d.web.annotation.HeaderData;
import com.astamuse.asta4d.web.annotation.PathVar;
import com.astamuse.asta4d.web.annotation.QueryParam;
import com.astamuse.asta4d.web.annotation.SessionData;

public class WebSpecialScopeConvertor implements AnnotationConvertor<Annotation, ContextData> {

    @Override
    public ContextData convert(Annotation originalAnnotation) {
        String scope = "";
        if (originalAnnotation instanceof CookieData) {
            scope = WebApplicationContext.SCOPE_COOKIE;
        } else if (originalAnnotation instanceof FlashData) {
            scope = WebApplicationContext.SCOPE_FLASH;
        } else if (originalAnnotation instanceof HeaderData) {
            scope = WebApplicationContext.SCOPE_HEADER;
        } else if (originalAnnotation instanceof PathVar) {
            scope = WebApplicationContext.SCOPE_PATHVAR;
        } else if (originalAnnotation instanceof QueryParam) {
            scope = WebApplicationContext.SCOPE_QUERYPARAM;
        } else if (originalAnnotation instanceof SessionData) {
            scope = WebApplicationContext.SCOPE_SESSION;
        }

        try {
            Method nameMethod = originalAnnotation.annotationType().getMethod("name");
            String name = (String) nameMethod.invoke(originalAnnotation);

            Method typeUnMatchMethod = originalAnnotation.annotationType().getMethod("typeUnMatch");
            TypeUnMacthPolicy typeUnMatch = (TypeUnMacthPolicy) typeUnMatchMethod.invoke(originalAnnotation);

            return gen(scope, name, typeUnMatch);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private ContextData gen(final String scope, final String name, final TypeUnMacthPolicy typeUnMatch) {
        return new ContextData() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return ContextData.class;
            }

            @Override
            public String scope() {
                return scope;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public TypeUnMacthPolicy typeUnMatch() {
                return typeUnMatch;
            }
        };
    }

}
