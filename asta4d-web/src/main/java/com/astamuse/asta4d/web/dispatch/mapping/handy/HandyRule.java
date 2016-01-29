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

package com.astamuse.asta4d.web.dispatch.mapping.handy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleHelper;

public class HandyRule extends HandyRuleWithHandler {

    public HandyRule(UrlMappingRule rule) {
        super(rule);
    }

    public HandyRule priority(int priority) {
        rule.setPriority(priority);
        return this;
    }

    public HandyRule var(String key, Object value) {
        Map<String, Object> map = rule.getExtraVarMap();
        if (map == null) {
            map = new HashMap<String, Object>();
            rule.setExtraVarMap(map);
        }
        map.put(key, value);
        return this;
    }

    public HandyRule attribute(String attribute) {
        List<String> attrList = rule.getAttributeList();
        attrList.add(attribute);
        return this;
    }

    public HandyRule id(String id) {
        this.var(UrlMappingRuleHelper.ID_VAR_NAME, id);
        return this;
    }
}
