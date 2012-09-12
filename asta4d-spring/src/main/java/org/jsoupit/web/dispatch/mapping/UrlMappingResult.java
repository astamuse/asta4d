package org.jsoupit.web.dispatch.mapping;

import java.util.Map;

public class UrlMappingResult {

    private UrlMappingRule rule;

    private Map<String, String> pathVarMap;

    public UrlMappingResult() {
        //
    }

    public UrlMappingResult(UrlMappingRule rule, Map<String, String> pathVarMap) {
        super();
        this.rule = rule;
        this.pathVarMap = pathVarMap;
    }

    public UrlMappingRule getRule() {
        return rule;
    }

    public void setRule(UrlMappingRule rule) {
        this.rule = rule;
    }

    public Map<String, String> getPathVarMap() {
        return pathVarMap;
    }

    public void setPathVarMap(Map<String, String> pathVarMap) {
        this.pathVarMap = pathVarMap;
    }

}
