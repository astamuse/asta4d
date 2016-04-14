package com.astamuse.asta4d.web.dispatch.mapping.handy;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.handy.base.HandlerConfigurableRule;

public class HandyRuleAfterAttr<B extends HandyRuleAfterAttr<?, ?>, C extends HandyRuleAfterHandler<?>> extends HandyRuleAfterHandler<C>
        implements HandlerConfigurableRule<B> {

    public HandyRuleAfterAttr(UrlMappingRule rule) {
        super(rule);
    }

    /* The following overriding is not necessary but we have to override to address the compile error due to Java's bad type inference ability */
    @Override
    public B handler(Object... handlerList) {
        return HandlerConfigurableRule.super.handler(handlerList);
    }

}
