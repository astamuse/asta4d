package com.astamuse.asta4d.web.dispatch;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingResult;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public interface DispatcherRuleExtractor {

    public UrlMappingResult findMappedRule(HttpServletRequest request, List<UrlMappingRule> ruleList);

}
