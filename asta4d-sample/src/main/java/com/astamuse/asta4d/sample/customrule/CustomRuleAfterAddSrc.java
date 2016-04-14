package com.astamuse.asta4d.sample.customrule;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.handy.HandyRuleAfterAddSrc;
import com.astamuse.asta4d.web.dispatch.mapping.handy.HandyRuleAfterAttr;
import com.astamuse.asta4d.web.dispatch.mapping.handy.HandyRuleAfterHandler;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CustomRuleAfterAddSrc extends HandyRuleAfterAddSrc<CustomRuleAfterAddSrc, HandyRuleAfterAttr<?, ?>, HandyRuleAfterHandler<?>>
        implements CustomRuleGroupConfigurable<CustomRuleAfterAddSrc>, CustomRuleBuilder {

    public CustomRuleAfterAddSrc(UrlMappingRule rule) {
        super(rule);
    }

}
