package com.astamuse.asta4d.web.dispatch.mapping.handy;

import com.astamuse.asta4d.web.dispatch.DispatcherRuleMatcher;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleSet;
import com.astamuse.asta4d.web.dispatch.mapping.handy.base.AttrConfigurableRule;

public class HandyRuleAfterAddSrc<A extends HandyRuleAfterAddSrc<?, ?, ?>, B extends HandyRuleAfterAttr<?, ?>, C extends HandyRuleAfterHandler<?>>
        extends HandyRuleAfterAttr<B, C>implements AttrConfigurableRule<A>, HandyRuleBuilder {

    public HandyRuleAfterAddSrc(UrlMappingRule rule) {
        super(rule);
    }

    public <D extends HandyRuleAfterAddSrcAndTarget<?>> D reMapTo(String ruleId) {
        this.var(UrlMappingRuleSet.REMAP_ID_VAR_NAME, ruleId);
        return buildHandyRuleAfterAddSrcAndTarget(rule);
    }

    /* The following overriding is not necessary but we have to override to address the compile error due to Java's bad type inference ability */
    @Override
    public A priority(int priority) {
        return AttrConfigurableRule.super.priority(priority);
    }

    @Override
    public A pathVar(String key, Object value) {
        return AttrConfigurableRule.super.pathVar(key, value);
    }

    @Override
    public A var(String key, Object value) {
        return AttrConfigurableRule.super.var(key, value);
    }

    @Override
    public A attribute(String attribute) {
        return AttrConfigurableRule.super.attribute(attribute);
    }

    @Override
    public A id(String id) {
        return AttrConfigurableRule.super.id(id);
    }

    @Override
    public A matcher(DispatcherRuleMatcher ruleMatcher) {
        return AttrConfigurableRule.super.matcher(ruleMatcher);
    }

}
