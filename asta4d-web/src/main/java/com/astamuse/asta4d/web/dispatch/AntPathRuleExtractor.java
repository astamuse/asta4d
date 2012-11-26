package com.astamuse.asta4d.web.dispatch;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.astamuse.asta4d.web.copyleft.SpringAntPathMatcher;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingResult;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public class AntPathRuleExtractor implements DispatcherRuleExtractor {
    private SpringAntPathMatcher pathMatcher = new SpringAntPathMatcher();

    @Override
    public UrlMappingResult findMappedRule(HttpServletRequest request, List<UrlMappingRule> ruleList) {
        try {
            String uri = URLDecoder.decode(request.getRequestURI(), "UTF-8");
            String contextPath = request.getContextPath();
            uri = uri.substring(contextPath.length());

            String method = request.getMethod().toUpperCase();

            UrlMappingResult mappingResult = null;
            String srcPath;
            HttpMethod ruleMethod;
            for (UrlMappingRule rule : ruleList) {
                ruleMethod = rule.getMethod();
                if (ruleMethod != null) {
                    if (!ruleMethod.toString().equals(method)) {
                        continue;
                    }
                }
                srcPath = rule.getSourcePath();
                if (pathMatcher.match(srcPath, uri)) {
                    mappingResult = new UrlMappingResult();
                    Map<String, Object> pathVarMap = new HashMap<>();
                    pathVarMap.putAll(pathMatcher.extractUriTemplateVariables(srcPath, uri));
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
