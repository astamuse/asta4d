package com.astamuse.asta4d.sample.customrule;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.handy.HandyRuleAfterAddSrcAndTarget;

public class CustomRuleAfterAddSrcAndTarget extends HandyRuleAfterAddSrcAndTarget<CustomRuleAfterAddSrcAndTarget>
        implements CustomRuleGroupConfigurable<CustomRuleAfterAddSrcAndTarget> {

    public CustomRuleAfterAddSrcAndTarget(UrlMappingRule rule) {
        super(rule);
    }

}
