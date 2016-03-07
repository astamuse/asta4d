/*
 * Copyright 2012 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.astamuse.asta4d.web.dispatch.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.util.IdGenerator;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.dispatch.DispatcherRuleMatcher;
import com.astamuse.asta4d.web.dispatch.HttpMethod;
import com.astamuse.asta4d.web.dispatch.HttpMethod.ExtendHttpMethod;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerInterceptor;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerResultHolder;
import com.astamuse.asta4d.web.util.bean.DeclareInstanceAdapter;
import com.astamuse.asta4d.web.util.bean.DeclareInstanceUtil;

/**
 * 
 * @author e-ryu
 *
 * @param <H>
 *            rule with handler configurable
 * @param <T>
 *            rule with target decided
 */
public abstract class UrlMappingRuleSet<H, T> {

    public final static String ID_VAR_NAME = UrlMappingRuleSet.class.getName() + "-rule-id";

    public final static String REMAP_ID_VAR_NAME = UrlMappingRuleSet.class.getName() + "-remap-rule-id";

    public final static int DEFAULT_PRIORITY = 0;

    protected final static AtomicInteger Sequencer = new AtomicInteger();

    protected final static class InterceptorHolder {
        String attribute;
        RequestHandlerInterceptor interceptor;

        public InterceptorHolder(String attribute, RequestHandlerInterceptor interceptor) {
            super();
            this.attribute = attribute;
            this.interceptor = interceptor;
        }

    }

    protected final static class RequestHandlerHolder {
        String attribute;
        Object handler;

        public RequestHandlerHolder(String attribute, Object handler) {
            super();
            this.attribute = attribute;
            this.handler = handler;
        }

    }

    protected static class InterceptorWrapper implements RequestHandlerInterceptor {

        final static String InterceptorInstanceCacheKey = InterceptorWrapper.class.getName() + "#InterceptorInstanceCacheKey";

        DeclareInstanceAdapter adapter;

        String id;

        InterceptorWrapper(DeclareInstanceAdapter adapter) {
            this.adapter = adapter;
            this.id = InterceptorInstanceCacheKey + "##" + IdGenerator.createId();
        }

        @Override
        public void preHandle(UrlMappingRule rule, RequestHandlerResultHolder holder) {
            RequestHandlerInterceptor interceptor = (RequestHandlerInterceptor) adapter.asTargetInstance();
            Context.getCurrentThreadContext().setData(id, interceptor);
            interceptor.preHandle(rule, holder);
        }

        @Override
        public void postHandle(UrlMappingRule rule, RequestHandlerResultHolder holder, ExceptionHandler exceptionHandler) {
            // retrieve the instance that actually was executed
            RequestHandlerInterceptor interceptor = Context.getCurrentThreadContext().getData(id);
            interceptor.postHandle(rule, holder, exceptionHandler);
        }
    }

    protected HttpMethod defaultMethod = HttpMethod.GET;

    protected List<InterceptorHolder> interceptorHolderList = new ArrayList<>();

    protected List<RequestHandlerHolder> defaultHandlerList = new ArrayList<>();

    protected List<UrlMappingRule> ruleList = new ArrayList<>();

    protected List<UrlMappingRuleRewriter> userCustomizedRuleRewriterList = new ArrayList<>();

    protected DispatcherRuleMatcher defaultRuleMatcher = WebApplicationConfiguration.getWebApplicationConfiguration().getRuleMatcher();

    public void setDefaultMethod(HttpMethod defaultMethod) {
        this.defaultMethod = defaultMethod;
    }

    public void addRequestHandlerInterceptor(String attribute, Object... interceptorList) {
        RequestHandlerInterceptor interceptor;
        Object instance;
        for (Object obj : interceptorList) {
            instance = DeclareInstanceUtil.createInstance(obj);
            if (instance instanceof RequestHandlerInterceptor) {
                interceptor = (RequestHandlerInterceptor) instance;
                interceptorHolderList.add(new InterceptorHolder(attribute, interceptor));
            } else if (instance instanceof DeclareInstanceAdapter) {
                interceptor = new InterceptorWrapper((DeclareInstanceAdapter) instance);
                interceptorHolderList.add(new InterceptorHolder(attribute, interceptor));
            }

        }
    }

    public void addRequestHandlerInterceptor(Object... interceptorList) {
        addRequestHandlerInterceptor(null, interceptorList);
    }

    public void addDefaultRequestHandler(String attribute, Object... handlerList) {
        for (Object handler : handlerList) {
            defaultHandlerList.add(new RequestHandlerHolder(attribute, handler));
        }
    }

    public void addDefaultRequestHandler(Object... handlerList) {
        addDefaultRequestHandler(null, handlerList);
    }

    public void addRuleRewriter(UrlMappingRuleRewriter ruleRewriter) {
        userCustomizedRuleRewriterList.add(ruleRewriter);
    }

    protected UrlMappingRule searchRuleById(List<UrlMappingRule> list, String id) {
        UrlMappingRule result = null;
        Object ruleId;
        for (UrlMappingRule rule : list) {
            ruleId = rule.extraVar(ID_VAR_NAME);
            if (ruleId == null) {
                continue;
            } else if (ruleId.equals(id)) {
                result = rule;
                break;
            }
        }
        if (result == null) {
            throw new IllegalArgumentException("Can not find a rule is identified by given id:" + id);
        } else {
            return result;
        }
    }

    public List<UrlMappingRule> getArrangedRuleList() {
        List<UrlMappingRule> arrangedRuleList = new ArrayList<>(ruleList);

        // user customized rewriting at first
        for (UrlMappingRule rule : arrangedRuleList) {
            for (UrlMappingRuleRewriter rewriter : userCustomizedRuleRewriterList) {
                rewriter.rewrite(rule);
            }
        }

        // config remapped rule
        Object reMapId;
        UrlMappingRule copyFromRule;
        for (UrlMappingRule rule : arrangedRuleList) {
            reMapId = rule.extraVar(REMAP_ID_VAR_NAME);
            if (reMapId == null) {
                continue;
            }

            copyFromRule = searchRuleById(arrangedRuleList, reMapId.toString());

            List<String> originalAttrList = rule.getAttributeList();
            Map<String, Object> originalVarMap = rule.getExtraVarMap();
            int originalPriority = rule.getPriority();

            rule.setAttributeList(new ArrayList<>(copyFromRule.getAttributeList()));
            rule.setExtraVarMap(new HashMap<>(copyFromRule.getExtraVarMap()));
            rule.setPriority(copyFromRule.getPriority());

            rule.setHandlerList(new ArrayList<>(copyFromRule.getHandlerList()));
            rule.setInterceptorList(new ArrayList<>(copyFromRule.getInterceptorList()));
            rule.setResultTransformerList(new ArrayList<>(copyFromRule.getResultTransformerList()));

            // add original configurations back
            if (originalAttrList != null) {
                rule.getAttributeList().addAll(originalAttrList);
            }

            if (originalVarMap != null) {
                rule.getExtraVarMap().putAll(originalVarMap);
            }

            if (originalPriority != DEFAULT_PRIORITY) {
                rule.setPriority(originalPriority);
            }
        }

        // set interceptor
        List<RequestHandlerInterceptor> interceptorList;
        for (UrlMappingRule rule : arrangedRuleList) {
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

        // set default request handler
        List<Object> requestHandlerList;
        for (UrlMappingRule rule : arrangedRuleList) {
            requestHandlerList = new ArrayList<>();
            for (RequestHandlerHolder handlerHolder : defaultHandlerList) {
                Object handler = DeclareInstanceUtil.createInstance(handlerHolder.handler);
                if (handlerHolder.attribute == null) {
                    requestHandlerList.add(handler);
                } else if (rule.hasAttribute(handlerHolder.attribute)) {
                    requestHandlerList.add(handler);
                }
            }
            requestHandlerList.addAll(rule.getHandlerList());
            rule.setHandlerList(requestHandlerList);
        }

        for (UrlMappingRule rule : arrangedRuleList) {
            List<UrlMappingRuleRewriter> list = UrlMappingRuleSetHelper.getBeforeSortRuleRewritter(rule);
            if (list != null) {
                for (UrlMappingRuleRewriter rewriter : list) {
                    rewriter.rewrite(rule);
                }
            }
        }

        // sort by priority
        Collections.sort(arrangedRuleList, new Comparator<UrlMappingRule>() {
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

        return arrangedRuleList;
    }

    public abstract H add(String sourcePath);

    public abstract T add(String sourcePath, String targetPath);

    public abstract H add(HttpMethod method, String sourcePath);

    public abstract T add(HttpMethod method, String sourcePath, String targetPath);

    public abstract H add(ExtendHttpMethod extendMethod, String sourcePath);

    public abstract T add(ExtendHttpMethod extendMethod, String sourcePath, String targetPath);
}
