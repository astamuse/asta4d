package com.astamuse.asta4d.web.dispatch.mapping.handy;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.handy.base.AttrConfigurableRule;
import com.astamuse.asta4d.web.dispatch.mapping.handy.base.HandyRuleBase;

public class HandyRuleAfterAddSrcAndTarget<D extends HandyRuleAfterAddSrcAndTarget<?>> extends HandyRuleBase
        implements AttrConfigurableRule<D> {

    public HandyRuleAfterAddSrcAndTarget(UrlMappingRule rule) {
        super(rule);
    }

}
