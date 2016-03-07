package com.astamuse.asta4d.web.dispatch.mapping.handy.rest;

import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;

public interface JsonSupportRuleSet {

    default void registerJsonTransformer(ResultTransformer transformer) {
        JsonSupportRuleHelper.registeredTransformer = transformer;
    }
}
