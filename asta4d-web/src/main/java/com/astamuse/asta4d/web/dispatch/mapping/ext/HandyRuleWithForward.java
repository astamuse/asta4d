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

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.request.MultiResultHolder;
import com.astamuse.asta4d.web.dispatch.request.transformer.SimpleTypeMatchTransformer;
import com.astamuse.asta4d.web.dispatch.response.provider.HeaderInfoProvider;

public class HandyRuleWithForward {

    protected UrlMappingRule rule;

    public HandyRuleWithForward(UrlMappingRule rule) {
        this.rule = rule;
    }

    private HandyRuleWithForward _forward(Object result, Object target) {
        rule.getResultTransformerList().add(new SimpleTypeMatchTransformer(result, target));
        return this;
    }

    public HandyRuleWithForward forward(Object result, String targetPath) {
        return this._forward(result, targetPath);
    }

    public HandyRuleWithForward forward(Object result, String targetPath, int status) {
        MultiResultHolder mrh = new MultiResultHolder();
        mrh.addResult(new HeaderInfoProvider(status));
        mrh.addResult(targetPath);
        return this._forward(result, mrh);
    }

    public HandyRuleWithForward redirect(Object result, String targetPath) {
        return this._forward(result, "redirect:" + targetPath);
    }

    public void forward(String targetPath) {
        this.forward(null, targetPath);
    }

    public void forward(String targetPath, int status) {
        this.forward(null, targetPath, status);
    }

    public void redirect(String targetPath) {
        this.redirect(null, targetPath);
    }

    public void json() {
        (new HandyRuleWithAttrOnly(rule)).var(UrlMappingRuleHelper.RULE_TYPE_VAR_NAME, UrlMappingRuleHelper.RULE_TYPE_JSON);
    }

    public void rest() {
        (new HandyRuleWithAttrOnly(rule)).var(UrlMappingRuleHelper.RULE_TYPE_VAR_NAME, UrlMappingRuleHelper.RULE_TYPE_REST);
    }
}
