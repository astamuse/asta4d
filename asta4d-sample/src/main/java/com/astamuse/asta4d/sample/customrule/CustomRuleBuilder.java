package com.astamuse.asta4d.sample.customrule;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.handy.HandyRuleBuilder;

@SuppressWarnings("unchecked")
public interface CustomRuleBuilder extends HandyRuleBuilder {

    @Override
    default CustomRuleAfterAddSrc buildHandyRuleAfterAddSrc(UrlMappingRule rule) {
        return new CustomRuleAfterAddSrc(rule);
    }

    @Override
    default CustomRuleAfterAddSrcAndTarget buildHandyRuleAfterAddSrcAndTarget(UrlMappingRule rule) {
        return new CustomRuleAfterAddSrcAndTarget(rule);
    }

}
