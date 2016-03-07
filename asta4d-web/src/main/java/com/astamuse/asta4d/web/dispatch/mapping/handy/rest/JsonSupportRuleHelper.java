package com.astamuse.asta4d.web.dispatch.mapping.handy.rest;

import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.DefaultExceptionTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.DefaultJsonTransformer;

public class JsonSupportRuleHelper {

    static ResultTransformer registeredTransformer = null;

    static final ResultTransformer FallbackJsonTransformer = new DefaultJsonTransformer();

    static final ResultTransformer ExceptionTransformer = new DefaultExceptionTransformer();

}
