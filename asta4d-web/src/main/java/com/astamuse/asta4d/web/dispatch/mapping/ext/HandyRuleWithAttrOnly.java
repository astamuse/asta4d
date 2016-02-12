/*
 * Copyright 2012 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.astamuse.asta4d.web.dispatch.mapping.ext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.astamuse.asta4d.web.dispatch.DispatcherRuleMatcher;
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

    public HandyRuleWithAttrOnly pathVar(String key, Object value) {
        Map<String, Object> map = rule.getExtraVarMap();
        if (map == null) {
            map = new HashMap<String, Object>();
        }
        map.put(key, value);
        rule.setExtraVarMap(map);
        return this;
    }

    public HandyRuleWithAttrOnly var(String key, Object value) {
        return pathVar(key, value);
    }

    public HandyRuleWithAttrOnly attribute(String attribute) {
        List<String> attrList = rule.getAttributeList();
        attrList.add(attribute);
        return this;
    }

    public HandyRuleWithAttrOnly id(String id) {
        this.var(UrlMappingRuleHelper.ID_VAR_NAME, id);
        return this;
    }

    public HandyRuleWithAttrOnly matcher(DispatcherRuleMatcher ruleMatcher) {
        rule.setRuleMatcher(ruleMatcher);
        return this;
    }

}
