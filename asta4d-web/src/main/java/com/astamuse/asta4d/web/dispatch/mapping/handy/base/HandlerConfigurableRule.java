package com.astamuse.asta4d.web.dispatch.mapping.handy.base;

import java.util.List;

import com.astamuse.asta4d.web.util.bean.DeclareInstanceUtil;

public interface HandlerConfigurableRule<T extends HandlerConfigurableRule<?>> extends HandyRuleConfigurable {

    default T handler(Object... handlerList) {
        configureRule(rule -> {
            List<Object> list = rule.getHandlerList();
            for (Object handler : handlerList) {
                list.add(DeclareInstanceUtil.createInstance((handler)));
            }
        });
        return (T) this;
    }
}
