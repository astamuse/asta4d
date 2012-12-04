package com.astamuse.asta4d.web.dispatch.mapping.ext;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public class HandyRuleWithRemap extends HandyRule {

    public HandyRuleWithRemap(UrlMappingRule rule) {
        super(rule);
    }

    public void reMapTo(String ruleId) {
        this.var(UrlMappingRuleHelper.REMAP_ID_VAR_NAME, ruleId);
    }

}
