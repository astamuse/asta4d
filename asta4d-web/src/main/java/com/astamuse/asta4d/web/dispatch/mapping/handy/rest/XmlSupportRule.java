package com.astamuse.asta4d.web.dispatch.mapping.handy.rest;

import java.util.List;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleSetHelper;
import com.astamuse.asta4d.web.dispatch.mapping.handy.base.HandyRuleConfigurable;
import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;

public interface XmlSupportRule extends HandyRuleConfigurable {

    public static final String JSON_RESULT_TRANSFORMER = XmlSupportRule.class.getName() + "#JSON_RESULT_TRANSFORMER";

    default void xml() {
        this.configureRule(rule -> {
            List<ResultTransformer> transformerList = rule.getResultTransformerList();
            if (!transformerList.isEmpty()) {
                throw new RuntimeException(
                        "Cannot declare json transforming on a rule in which there has been forward/redirect declaration.");
            }
            if (XmlSupportRuleHelper.registeredTransformer != null) {
                transformerList.add(XmlSupportRuleHelper.registeredTransformer);
            }
            transformerList.add(XmlSupportRuleHelper.ExceptionTransformer);
            transformerList.add(XmlSupportRuleHelper.FallbackXmlTransformer);
            UrlMappingRuleSetHelper.setRuleType(rule, "xml");

        });
    }

}
