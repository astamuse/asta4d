package com.astamuse.asta4d.web.annotation.convertor;

import java.lang.annotation.Annotation;

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
        if (originalAnnotation instanceof CookieData) {
            return gen(WebApplicationContext.SCOPE_COOKIE, ((CookieData) originalAnnotation).name());
        } else if (originalAnnotation instanceof FlashData) {
            return gen(WebApplicationContext.SCOPE_FLASH, ((FlashData) originalAnnotation).name());
        } else if (originalAnnotation instanceof HeaderData) {
            return gen(WebApplicationContext.SCOPE_HEADER, ((HeaderData) originalAnnotation).name());
        } else if (originalAnnotation instanceof PathVar) {
            return gen(WebApplicationContext.SCOPE_PATHVAR, ((PathVar) originalAnnotation).name());
        } else if (originalAnnotation instanceof QueryParam) {
            return gen(WebApplicationContext.SCOPE_QUERYPARAM, ((QueryParam) originalAnnotation).name());
        } else if (originalAnnotation instanceof SessionData) {
            return gen(WebApplicationContext.SCOPE_SESSION, ((SessionData) originalAnnotation).name());
        } else {
            return null;
        }
    }

    private ContextData gen(final String scope, final String name) {
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
            public boolean reverse() {
                return false;
            }

            @Override
            public String name() {
                return name;
            }
        };
    }

}
