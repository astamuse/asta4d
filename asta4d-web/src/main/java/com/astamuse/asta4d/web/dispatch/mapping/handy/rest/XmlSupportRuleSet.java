package com.astamuse.asta4d.web.dispatch.mapping.handy.rest;

import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;

public interface XmlSupportRuleSet {

    default void registerXmlTransformer(ResultTransformer transformer) {
        XmlSupportRuleHelper.registeredTransformer = transformer;
    }
}
