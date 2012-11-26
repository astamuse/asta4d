package com.astamuse.asta4d.web.dispatch.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.astamuse.asta4d.web.dispatch.response.ContentWriter;
import com.astamuse.asta4d.web.dispatch.response.DefaultContentWriter;

@Retention(RetentionPolicy.RUNTIME)
public @interface ContentProvider {
    Class<? extends ContentWriter> writer() default DefaultContentWriter.class;
}
