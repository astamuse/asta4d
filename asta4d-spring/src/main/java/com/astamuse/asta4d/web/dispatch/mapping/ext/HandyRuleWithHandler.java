package com.astamuse.asta4d.web.dispatch.mapping.ext;

import java.util.List;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.util.DeclareInstanceUtil;

public class HandyRuleWithHandler extends HandyRuleWithForward {

    public HandyRuleWithHandler(UrlMappingRule rule) {
        super(rule);
    }

    public HandyRuleWithHandler handler(Object... handlerList) {
        List<Object> list = rule.getHandlerList();
        for (Object handler : handlerList) {
            list.add(DeclareInstanceUtil.createInstance((handler)));
        }
        return this;
    }

}
