package com.astamuse.asta4d.web.dispatch.mapping.ext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public class HandyRuleWithAttrOnly {

    private UrlMappingRule rule;

    public HandyRuleWithAttrOnly(UrlMappingRule rule) {
        this.rule = rule;
    }

    public HandyRuleWithAttrOnly priority(int priority) {
        rule.setPriority(priority);
        return this;
    }

    public HandyRuleWithAttrOnly var(String key, Object value) {
        Map<String, Object> map = rule.getExtraVarMap();
        if (map == null) {
            map = new HashMap<String, Object>();
        }
        map.put(key, value);
        rule.setExtraVarMap(map);
        return this;
    }

    public HandyRuleWithAttrOnly attribute(String attribute) {
        List<String> attrList = rule.getAttributeList();
        attrList.add(attribute);
        return this;
    }

}
