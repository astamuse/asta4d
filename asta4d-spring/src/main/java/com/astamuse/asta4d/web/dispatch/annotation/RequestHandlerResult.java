package com.astamuse.asta4d.web.dispatch.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.web.dispatch.RequestDispatcher;

@Retention(RetentionPolicy.RUNTIME)
@ContextData(name = RequestDispatcher.KEY_REQUEST_HANDLER_RESULT)
public @interface RequestHandlerResult {

}
