package com.astamuse.asta4d.web.dispatch.mapping.handy.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.astamuse.asta4d.web.dispatch.DispatcherRuleMatcher;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleSet;

@SuppressWarnings({ "unchecked", "rawtypes" })
public interface AttrConfigurableRule<T extends AttrConfigurableRule> extends HandyRuleConfigurable {

    default T priority(int priority) {
        configureRule(rule -> {
            rule.setPriority(priority);
        });
        return (T) this;
    }

    default T pathVar(String key, Object value) {
        return var(key, value);
    }

    default T var(String key, Object value) {
        configureRule(rule -> {
            Map<String, Object> map = rule.getExtraVarMap();
            if (map == null) {
                map = new HashMap<String, Object>();
            }
            map.put(key, value);
            rule.setExtraVarMap(map);
        });
        return (T) this;
    }

    default T attribute(String attribute) {
        configureRule(rule -> {
            List<String> attrList = rule.getAttributeList();
            attrList.add(attribute);
        });
        return (T) this;
    }

    default T id(String id) {
        configureRule(rule -> {
            this.var(UrlMappingRuleSet.ID_VAR_NAME, id);
        });
        return (T) this;
    }

    default T matcher(DispatcherRuleMatcher ruleMatcher) {
        configureRule(rule -> {
            rule.setRuleMatcher(ruleMatcher);
        });
        return (T) this;
    }
}
