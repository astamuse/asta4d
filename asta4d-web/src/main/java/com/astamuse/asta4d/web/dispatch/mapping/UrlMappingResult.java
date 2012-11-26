package com.astamuse.asta4d.web.dispatch.mapping;

import java.util.Map;

public class UrlMappingResult {

    private UrlMappingRule rule;

    private Map<String, Object> pathVarMap;

    public UrlMappingResult() {
        //
    }

    public UrlMappingResult(UrlMappingRule rule, Map<String, Object> pathVarMap) {
        super();
        this.rule = rule;
        this.pathVarMap = pathVarMap;
    }

    public UrlMappingRule getRule() {
        return rule.asUnmodifiable();
    }

    public void setRule(UrlMappingRule rule) {
        this.rule = rule;
    }

    public Map<String, Object> getPathVarMap() {
        return pathVarMap;
    }

    public void setPathVarMap(Map<String, Object> pathVarMap) {
        this.pathVarMap = pathVarMap;
    }

}
