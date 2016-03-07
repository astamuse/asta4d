package com.astamuse.asta4d.web.dispatch.mapping.handy;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.handy.base.HandyRuleBase;
import com.astamuse.asta4d.web.dispatch.mapping.handy.rest.JsonSupportRule;
import com.astamuse.asta4d.web.dispatch.mapping.handy.rest.XmlSupportRule;
import com.astamuse.asta4d.web.dispatch.mapping.handy.template.TemplateRuleWithForward;

public class HandyRuleAfterHandler<C extends HandyRuleAfterHandler<?>> extends HandyRuleBase
        implements TemplateRuleWithForward<C>, JsonSupportRule, XmlSupportRule {

    public HandyRuleAfterHandler(UrlMappingRule rule) {
        super(rule);
    }

}
