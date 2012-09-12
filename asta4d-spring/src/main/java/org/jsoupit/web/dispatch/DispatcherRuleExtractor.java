package org.jsoupit.web.dispatch;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jsoupit.web.dispatch.mapping.UrlMappingResult;
import org.jsoupit.web.dispatch.mapping.UrlMappingRule;

public interface DispatcherRuleExtractor {

    public UrlMappingResult findMappedRule(HttpServletRequest request, List<UrlMappingRule> ruleList);

}
