package com.astamuse.asta4d.misc.spring.mvc.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import com.astamuse.asta4d.web.dispatch.DispatcherRuleExtractor;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingResult;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public class AntPathRuleExtractor implements DispatcherRuleExtractor {
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public UrlMappingResult findMappedRule(HttpServletRequest request, List<UrlMappingRule> ruleList) {
        try {
            String uri = URLDecoder.decode(request.getRequestURI(), "UTF-8");
            String contextPath = request.getContextPath();
            uri = uri.substring(contextPath.length());

            RequestMethod method = RequestMethod.valueOf(request.getMethod());

            UrlMappingResult mappingResult = null;
            String srcUrl;
            for (UrlMappingRule rule : ruleList) {
                // TODO we need support all method matching
                if (method != rule.getMethod()) {
                    continue;
                }
                srcUrl = rule.getSourceUrl();
                if (pathMatcher.match(srcUrl, uri)) {
                    mappingResult = new UrlMappingResult();
                    Map<String, String> pathVarMap = new HashMap<>();
                    pathVarMap.putAll(pathMatcher.extractUriTemplateVariables(srcUrl, uri));
                    mappingResult.setPathVarMap(pathVarMap);
                    mappingResult.setRule(rule);
                    break;
                }
            }

            return mappingResult;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }
}
