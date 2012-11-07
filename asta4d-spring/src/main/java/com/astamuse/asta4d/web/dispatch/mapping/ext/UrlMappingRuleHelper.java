package com.astamuse.asta4d.web.dispatch.mapping.ext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.astamuse.asta4d.web.dispatch.HttpMethod;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerInterceptor;
import com.astamuse.asta4d.web.dispatch.mapping.ResultDescriptor;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.util.DeclareInstanceUtil;

public class UrlMappingRuleHelper {

    public final static int DEFAULT_PRIORITY = 0;

    private final static AtomicInteger Sequencer = new AtomicInteger();

    private final static class InterceptorHolder {
        String attribute;
        RequestHandlerInterceptor interceptor;

        public InterceptorHolder(String attribute, RequestHandlerInterceptor interceptor) {
            super();
            this.attribute = attribute;
            this.interceptor = interceptor;
        }

    }

    private final static class GlobalForwardHolder {
        Object result;
        String targetPath;
        Integer status;
        boolean isRedirect;

        public GlobalForwardHolder(Object result, String targetPath, Integer status, boolean isRedirect) {
            super();
            this.result = result;
            this.targetPath = targetPath;
            this.status = status;
            this.isRedirect = isRedirect;
        }
    }

    private HttpMethod defaultMethod = HttpMethod.GET;

    private List<InterceptorHolder> interceptorHolderList = new ArrayList<>();

    private List<GlobalForwardHolder> forwardHolderList = new ArrayList<>();

    private List<UrlMappingRule> ruleList = new ArrayList<>();

    public void setDefaultMethod(HttpMethod defaultMethod) {
        this.defaultMethod = defaultMethod;
    }

    public void addRequestHandlerInterceptor(String attribute, Object... interceptorList) {
        RequestHandlerInterceptor interceptor;
        for (Object obj : interceptorList) {
            interceptor = (RequestHandlerInterceptor) DeclareInstanceUtil.createInstance(obj);
            interceptorHolderList.add(new InterceptorHolder(attribute, interceptor));
        }
    }

    public void addRequestHandlerInterceptor(Object... interceptorList) {
        addRequestHandlerInterceptor(null, interceptorList);
    }

    public void addGlobalForward(Object result, String targetPath, int status) {
        forwardHolderList.add(new GlobalForwardHolder(result, targetPath, status, false));
    }

    public void addGlobalForward(Object result, String targetPath) {
        forwardHolderList.add(new GlobalForwardHolder(result, targetPath, null, false));
    }

    public void addGlobalRedirect(Object result, String targetPath) {
        forwardHolderList.add(new GlobalForwardHolder(result, targetPath, null, true));
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
        List<RequestHandlerInterceptor> interceptorList;
        for (UrlMappingRule rule : sortedRuleList) {
            interceptorList = new ArrayList<>();
            for (InterceptorHolder iHolder : interceptorHolderList) {
                if (iHolder.attribute == null) {
                    interceptorList.add(iHolder.interceptor);
                } else if (rule.hasAttribute(iHolder.attribute)) {
                    interceptorList.add(iHolder.interceptor);
                }
            }
            rule.setInterceptorList(interceptorList);
        }

        // set global result forward
        for (UrlMappingRule rule : sortedRuleList) {
            // add global forward
            HandyRuleWithForward hf = new HandyRuleWithForward(rule);
            for (GlobalForwardHolder forwardHolder : forwardHolderList) {
                if (forwardHolder.isRedirect) {
                    hf.redirect(forwardHolder.result, forwardHolder.targetPath);
                } else if (forwardHolder.status == null) {
                    hf.forward(forwardHolder.result, forwardHolder.targetPath);
                } else {
                    hf.forward(forwardHolder.result, forwardHolder.targetPath, forwardHolder.status);
                }
            }

            // move the default forward rule to the last of the result list
            List<ResultDescriptor> resultList = rule.getContentProviderMap();
            int size = resultList.size();
            ResultDescriptor result, defaultResult = null;
            for (int i = size - 1; i >= 0; i--) {
                result = resultList.get(i);
                if (result.getResultTypeIdentifier() == null && result.getResultInstanceIdentifier() == null) {
                    resultList.remove(i);
                    defaultResult = result;
                    break;
                }
            }
            if (defaultResult != null) {
                resultList.add(defaultResult);
            }
            rule.setContentProviderMap(resultList);
        }// sortedRuleList loop

        return sortedRuleList;
    }

    private UrlMappingRule createDefaultRule(HttpMethod method, String sourcePath) {
        UrlMappingRule rule = new UrlMappingRule();
        ruleList.add(rule);

        rule.setMethod(method);
        rule.setSourcePath(sourcePath);
        rule.setSeq(Sequencer.incrementAndGet());
        rule.setPriority(DEFAULT_PRIORITY);

        return rule;
    }

    public HandyRule add(HttpMethod method, String sourcePath) {
        HandyRule handyRule = new HandyRule(createDefaultRule(method, sourcePath));
        return handyRule;
    }

    public HandyRuleWithAttrOnly add(HttpMethod method, String sourcePath, String targetPath) {
        UrlMappingRule rule = createDefaultRule(method, sourcePath);
        HandyRule handyRule = new HandyRule(rule);
        handyRule.forward(targetPath);
        return new HandyRuleWithAttrOnly(rule);
    }

    public HandyRule add(String sourcePath) {
        return add(defaultMethod, sourcePath);
    }

    public HandyRuleWithAttrOnly add(String sourcePath, String targetPath) {
        return add(defaultMethod, sourcePath, targetPath);
    }

}
