package com.astamuse.asta4d.web.dispatch.mapping;

import java.util.ArrayList;
import java.util.List;

public class UrlMappingRuleSetHelper {

    private final static String RULE_TYPE_VAR_NAME = UrlMappingRuleSetHelper.class.getName() + "-rule-type";

    private final static String BEFORE_SORT_RULE_REWRITTER_LIST_VAR_NAME = UrlMappingRuleSetHelper.class.getName() +
            "-before-sort-rule-rewritter-list";

    public static final void addBeforeSortRuleRewritter(UrlMappingRule rule, UrlMappingRuleRewriter rewritter) {
        @SuppressWarnings("unchecked")
        List<UrlMappingRuleRewriter> list = (List<UrlMappingRuleRewriter>) rule.getExtraVarMap()
                .get(BEFORE_SORT_RULE_REWRITTER_LIST_VAR_NAME);
        if (list == null) {
            list = new ArrayList<>();
            rule.getExtraVarMap().put(BEFORE_SORT_RULE_REWRITTER_LIST_VAR_NAME, list);
        }
        list.add(rewritter);
    }

    @SuppressWarnings("unchecked")
    public static final List<UrlMappingRuleRewriter> getBeforeSortRuleRewritter(UrlMappingRule rule) {
        return (List<UrlMappingRuleRewriter>) rule.getExtraVarMap().get(BEFORE_SORT_RULE_REWRITTER_LIST_VAR_NAME);
    }

    public static final void setRuleType(UrlMappingRule rule, String type) {
        rule.getExtraVarMap().put(RULE_TYPE_VAR_NAME, type);
    }

    public static final String getRuleType(UrlMappingRule rule) {
        return (String) rule.getExtraVarMap().get(RULE_TYPE_VAR_NAME);
    }
}
