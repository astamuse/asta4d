package com.astamuse.asta4d.web.dispatch.mapping.ext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.astamuse.asta4d.web.dispatch.HttpMethod;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerInterceptor;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public class UrlMappingRuleHelper {

    private final static class InterceptorHolder {
        String attribute;
        RequestHandlerInterceptor interceptor;

        public InterceptorHolder(String attribute, RequestHandlerInterceptor interceptor) {
            super();
            this.attribute = attribute;
            this.interceptor = interceptor;
        }

    }

    private HttpMethod defaultMethod = HttpMethod.GET;

    private List<InterceptorHolder> interceptorHolderList = new ArrayList<>();

    private List<UrlMappingRule> ruleList = new ArrayList<>();

    public void setDefaultMethod(HttpMethod defaultMethod) {
        this.defaultMethod = defaultMethod;
    }

    public void addRequestHandlerInterceptor(RequestHandlerInterceptor... interceptorList) {
        for (RequestHandlerInterceptor interceptor : interceptorList) {
            interceptorHolderList.add(new InterceptorHolder(null, interceptor));
        }
    }

    public void addRequestHandlerInterceptor(String attribute, RequestHandlerInterceptor... interceptorList) {
        for (RequestHandlerInterceptor interceptor : interceptorList) {
            interceptorHolderList.add(new InterceptorHolder(attribute, interceptor));
        }
    }

    public List<UrlMappingRule> getArrangedRuleList() {
        List<UrlMappingRule> sortedRuleList = new ArrayList<>(ruleList);
        Collections.sort(sortedRuleList, new Comparator<UrlMappingRule>() {
            @Override
            public int compare(UrlMappingRule r1, UrlMappingRule r2) {
                int pc = r1.getPriority() - r2.getPriority();
                if (pc != 0) {
                    return pc;
                } else {
                    return r1.getSeq() - r2.getSeq();
                }

            }

        });
        /*
        List<Object> handlerList;
        for (UrlMappingRule rule : sortedRuleList) {
            handlerList = rule.getHandlerList();
            if (handlerList == null) {
                handlerList = new ArrayList<>();
            }
            rule.setHandlerList(handlerList);
        }
        */

        // set interceptor
        List<String> attrList;
        List<RequestHandlerInterceptor> interceptorList;
        for (UrlMappingRule rule : sortedRuleList) {
            interceptorList = new ArrayList<>();
            attrList = rule.getAttributeList();
            for (InterceptorHolder iHolder : interceptorHolderList) {
                if (iHolder.attribute == null) {
                    interceptorList.add(iHolder.interceptor);
                } else if (attrList.contains(iHolder.attribute)) {
                    interceptorList.add(iHolder.interceptor);
                }
            }
            rule.setInterceptorList(interceptorList);
        }

        return sortedRuleList;
    }

    private HandyUrlMappingRule createDefaultRule(HttpMethod method, String sourcePath) {
        HandyUrlMappingRule rule = new HandyUrlMappingRule(method, sourcePath);
        ruleList.add(rule);
        return rule;
    }

    public HandyUrlMappingRule add(HttpMethod method, String sourcePath, Object contentProvider, Object... handlerList) {
        HandyUrlMappingRule rule = createDefaultRule(method, sourcePath);
        if (contentProvider != null) {
            rule.forward(null, contentProvider);
        }
        rule.handler(handlerList);
        return rule;
    }

    public HandyUrlMappingRule add(HttpMethod method, String sourcePath, String targetPath, Object... handlerList) {
        HandyUrlMappingRule rule = createDefaultRule(method, sourcePath);
        rule.forward(null, targetPath);
        rule.handler(handlerList);
        return rule;
    }

    public HandyUrlMappingRule add(HttpMethod method, String sourcePath, Object contentProvider) {
        HandyUrlMappingRule rule = createDefaultRule(method, sourcePath);
        if (contentProvider instanceof String) {
            rule.forward(null, contentProvider.toString());
        } else {
            rule.forward(null, contentProvider);
        }

        return rule;

    }

    public HandyUrlMappingRule add(String sourcePath, Object contentProvider, Object... handlerList) {
        return add(defaultMethod, sourcePath, contentProvider, handlerList);
    }

    public HandyUrlMappingRule add(String sourcePath, String targetPath, Object... handlerList) {
        return add(defaultMethod, sourcePath, targetPath, handlerList);
    }

    public HandyUrlMappingRule add(String sourcePath, Object contentProvider) {
        return add(defaultMethod, sourcePath, contentProvider);
    }

}
