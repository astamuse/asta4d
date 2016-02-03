package com.astamuse.asta4d.web.dispatch.mapping.handy.base;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public abstract class HandyRuleBase implements HandyRuleConfigurable {

    protected UrlMappingRule rule;

    public HandyRuleBase(UrlMappingRule rule) {
        this.rule = rule;
    }

    @Override
    public void configureRule(HandyRuleConfigurer configure) {
        configure.configure(rule);
    }

}
