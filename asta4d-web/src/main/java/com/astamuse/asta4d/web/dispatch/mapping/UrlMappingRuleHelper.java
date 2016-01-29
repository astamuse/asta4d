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
import java.util.LinkedList;
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
import com.astamuse.asta4d.web.dispatch.mapping.handy.HandyRule;
import com.astamuse.asta4d.web.dispatch.mapping.handy.HandyRuleWithAttrOnly;
import com.astamuse.asta4d.web.dispatch.mapping.handy.HandyRuleWithForward;
import com.astamuse.asta4d.web.dispatch.mapping.handy.HandyRuleWithRemap;
import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.Asta4DPageTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.DefaultExceptionTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.DefaultJsonTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.DefaultStringTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.DefaultTemplateNotFoundExceptionTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.SimpleTypeMatchTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.StopTransformer;
import com.astamuse.asta4d.web.util.bean.DeclareInstanceAdapter;
import com.astamuse.asta4d.web.util.bean.DeclareInstanceUtil;

public class UrlMappingRuleHelper {

    public final static String ID_VAR_NAME = UrlMappingRuleHelper.class.getName() + "-rule-id";

    public final static String REMAP_ID_VAR_NAME = UrlMappingRuleHelper.class.getName() + "-remap-rule-id";

    public final static String RULE_TYPE_VAR_NAME = UrlMappingRuleHelper.class.getName() + "-rule-type";

    public final static String RULE_TYPE_JSON = "JSON";

    public final static String RULE_TYPE_REST = "REST";

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

    private final static class RequestHandlerHolder {
        String attribute;
        Object handler;

        public RequestHandlerHolder(String attribute, Object handler) {
            super();
            this.attribute = attribute;
            this.handler = handler;
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

    private static class InterceptorWrapper implements RequestHandlerInterceptor {

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

    private HttpMethod defaultMethod = HttpMethod.GET;

    private List<InterceptorHolder> interceptorHolderList = new ArrayList<>();

    private List<RequestHandlerHolder> defaultHandlerList = new ArrayList<>();

    private List<GlobalForwardHolder> forwardHolderList = new ArrayList<>();

    private List<UrlMappingRule> ruleList = new ArrayList<>();

    private List<UrlMappingRuleRewriter> ruleRewriterList = new ArrayList<>();

    private ResultTransformer jsonTransformer = null;

    private ResultTransformer defaultJsonTransformer = new DefaultJsonTransformer();

    private ResultTransformer restTransformer = null;

    private ResultTransformer defaultRestTransformer = new StopTransformer();

    private DispatcherRuleMatcher defaultRuleMatcher = WebApplicationConfiguration.getWebApplicationConfiguration().getRuleMatcher();

    public void setDefaultMethod(HttpMethod defaultMethod) {
        this.defaultMethod = defaultMethod;
    }

    public void registerJsonTransformer(ResultTransformer transformer) {
        jsonTransformer = transformer;
    }

    public void registerRestTransformer(ResultTransformer transformer) {
        restTransformer = transformer;
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
        ruleRewriterList.add(ruleRewriter);
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

    private UrlMappingRule searchRuleById(List<UrlMappingRule> list, String id) {
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

        // rewrite rules at first
        for (UrlMappingRule rule : arrangedRuleList) {
            for (UrlMappingRuleRewriter rewriter : ruleRewriterList) {
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

        // reorganize the transformer list
        for (UrlMappingRule rule : arrangedRuleList) {
            Object ruleType = rule.extraVar(RULE_TYPE_VAR_NAME);
            if (RULE_TYPE_REST.equals(ruleType)) {
                reOrganizeRestTransformers(rule);
            } else if (RULE_TYPE_JSON.equals(ruleType)) {
                reOrganizeJsonTransformers(rule);
            } else {
                reOrganizeTemplateTransformers(rule);
            }
        } // sortedRuleList loop

        return arrangedRuleList;
    }

    private void reOrganizeTemplateTransformers(UrlMappingRule rule) {
        List<ResultTransformer> transformerList = rule.getResultTransformerList();
        // find out the default forward rule
        ResultTransformer transformer, defaultTransformer = null;

        int size = transformerList.size();
        for (int i = size - 1; i >= 0; i--) {
            transformer = transformerList.get(i);
            if (transformer instanceof SimpleTypeMatchTransformer) {
                if (((SimpleTypeMatchTransformer) transformer).isAsDefaultMatch()) {
                    defaultTransformer = transformer;
                    transformerList.remove(i);
                    break;
                }
            }
        }

        boolean hasHandler = !rule.getHandlerList().isEmpty();

        if (hasHandler) {
            // add global forward
            addGlobalForwardTransformers(rule);
            // add String transformers for non default forward rules(forward
            // by result)
            transformerList.add(new DefaultStringTransformer());

            // add global forward again for possible
            // exceptions on the above transformers
            addGlobalForwardTransformers(rule);
            // add String transformers for the global forword rules
            transformerList.add(new DefaultStringTransformer());
        }

        // add default forward rule
        if (defaultTransformer != null) {
            transformerList.add(defaultTransformer);
            // add default String transformers for the default forward rule
            transformerList.add(new DefaultStringTransformer());

            // add global forward of Throwable result again again (!!!) for
            // possible exceptions on the above transformers
            addGlobalForwardTransformers(rule);
            // add String transformers for the global throwable result
            // forword rules
            transformerList.add(new DefaultStringTransformer());
        }

        // add the last insured transformers
        transformerList.add(new DefaultTemplateNotFoundExceptionTransformer());
        transformerList.add(new DefaultExceptionTransformer());
        transformerList.add(new Asta4DPageTransformer());

        rule.setResultTransformerList(transformerList);
    }

    private void addGlobalForwardTransformers(UrlMappingRule rule) {
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
    }

    private void reOrganizeJsonTransformers(UrlMappingRule rule) {
        List<ResultTransformer> transformerList = new LinkedList<>();
        if (jsonTransformer != null) {
            transformerList.add(jsonTransformer);
        }

        transformerList.add(new DefaultExceptionTransformer());
        transformerList.add(defaultJsonTransformer);

        rule.setResultTransformerList(transformerList);
    }

    private void reOrganizeRestTransformers(UrlMappingRule rule) {
        List<ResultTransformer> transformerList = new LinkedList<>();
        if (restTransformer != null) {
            transformerList.add(restTransformer);
        }
        transformerList.add(new DefaultExceptionTransformer());
        transformerList.add(defaultRestTransformer);
        rule.setResultTransformerList(transformerList);
    }

    private UrlMappingRule createDefaultRule(HttpMethod method, ExtendHttpMethod extendMethod, String sourcePath) {
        UrlMappingRule rule = new UrlMappingRule();
        ruleList.add(rule);
        rule.setMethod(method);
        rule.setExtendMethod(extendMethod);
        rule.setSourcePath(sourcePath);
        rule.setSeq(Sequencer.incrementAndGet());
        rule.setPriority(DEFAULT_PRIORITY);
        rule.setRuleMatcher(defaultRuleMatcher);

        return rule;
    }

    public HandyRuleWithRemap add(String sourcePath) {
        return add(defaultMethod, sourcePath);
    }

    public HandyRuleWithAttrOnly add(String sourcePath, String targetPath) {
        return add(defaultMethod, sourcePath, targetPath);
    }

    public HandyRuleWithRemap add(HttpMethod method, String sourcePath) {
        HandyRuleWithRemap handyRule = new HandyRuleWithRemap(createDefaultRule(method, null, sourcePath));
        return handyRule;
    }

    public HandyRuleWithAttrOnly add(HttpMethod method, String sourcePath, String targetPath) {
        UrlMappingRule rule = createDefaultRule(method, null, sourcePath);
        HandyRule handyRule = new HandyRule(rule);
        handyRule.forward(targetPath);
        return new HandyRuleWithAttrOnly(rule);
    }

    public HandyRuleWithRemap add(ExtendHttpMethod extendMethod, String sourcePath) {
        HandyRuleWithRemap handyRule = new HandyRuleWithRemap(createDefaultRule(HttpMethod.UNKNOWN, extendMethod, sourcePath));
        return handyRule;
    }

    public HandyRuleWithAttrOnly add(ExtendHttpMethod extendMethod, String sourcePath, String targetPath) {
        UrlMappingRule rule = createDefaultRule(HttpMethod.UNKNOWN, extendMethod, sourcePath);
        HandyRule handyRule = new HandyRule(rule);
        handyRule.forward(targetPath);
        return new HandyRuleWithAttrOnly(rule);
    }

}
