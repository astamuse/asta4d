package com.astamuse.asta4d.web.dispatch.mapping.handy.base;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

@FunctionalInterface
public interface HandyRuleConfigurer {
    public void configure(UrlMappingRule rule);
}
