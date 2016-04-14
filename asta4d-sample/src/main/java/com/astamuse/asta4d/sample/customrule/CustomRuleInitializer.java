package com.astamuse.asta4d.sample.customrule;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleInitializer;

public class CustomRuleInitializer implements UrlMappingRuleInitializer<CustomRuleSet> {
    @Override
    public void initUrlMappingRules(CustomRuleSet rules) {
        rules.add("/", "index.html").id("top").group("somegroup");
        rules.add("/index.html").reMapTo("top");
        rules.add("/gohandler").group("handler-group").handler(new Object() {
            public void handler() {
                System.out.println("");
            }
        }).forward("handlerResult.html");
    }

}
