package com.astamuse.asta4d.web.dispatch.mapping.handy.rest;

import java.util.List;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleSetHelper;
import com.astamuse.asta4d.web.dispatch.mapping.handy.base.HandyRuleConfigurable;
import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;

public interface JsonSupportRule extends HandyRuleConfigurable {

    public static final String JSON_RESULT_TRANSFORMER = JsonSupportRule.class.getName() + "#JSON_RESULT_TRANSFORMER";

    default void json() {
        this.configureRule(rule -> {
            List<ResultTransformer> transformerList = rule.getResultTransformerList();
            if (!transformerList.isEmpty()) {
                throw new RuntimeException(
                        "Cannot declare json transforming on a rule in which there has been forward/redirect declaration.");
            }
            if (JsonSupportRuleHelper.registeredTransformer != null) {
                transformerList.add(JsonSupportRuleHelper.registeredTransformer);

            }
            transformerList.add(JsonSupportRuleHelper.ExceptionTransformer);
            transformerList.add(JsonSupportRuleHelper.FallbackJsonTransformer);

            UrlMappingRuleSetHelper.setRuleType(rule, "json");

        });
    }

}
