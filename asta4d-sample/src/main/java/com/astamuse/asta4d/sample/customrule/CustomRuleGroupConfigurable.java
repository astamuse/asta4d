package com.astamuse.asta4d.sample.customrule;

import com.astamuse.asta4d.web.dispatch.mapping.handy.base.HandyRuleConfigurable;

public interface CustomRuleGroupConfigurable<T> extends HandyRuleConfigurable {

    default T group(String group) {
        configureRule(rule -> {
            rule.getExtraVarMap().put("CustomGroup", group);
        });
        return (T) this;
    }

}
