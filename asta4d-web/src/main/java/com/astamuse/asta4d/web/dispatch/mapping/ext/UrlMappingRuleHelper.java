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

package com.astamuse.asta4d.web.dispatch.mapping.ext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.util.IdGenerator;
import com.astamuse.asta4d.web.dispatch.HttpMethod;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerInterceptor;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerResultHolder;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.SimpleTypeMatchTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.String2Asta4DPageTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.String2RedirctTransformer;
import com.astamuse.asta4d.web.util.DeclareInstanceAdapter;
import com.astamuse.asta4d.web.util.DeclareInstanceUtil;

public class UrlMappingRuleHelper {

    public final static String ID_VAR_NAME = UrlMappingRuleHelper.class.getName() + "-rule-id";

    public final static String REMAP_ID_VAR_NAME = UrlMappingRuleHelper.class.getName() + "-remap-rule-id";

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

    private final static String2Asta4DPageTransformer asta4dPageTransformer = new String2Asta4DPageTransformer();

    private final static String2RedirctTransformer redirectTransformer = new String2RedirctTransformer();

    private HttpMethod defaultMethod = HttpMethod.GET;

    private List<InterceptorHolder> interceptorHolderList = new ArrayList<>();

    private List<RequestHandlerHolder> defaultHandlerList = new ArrayList<>();

    private List<GlobalForwardHolder> forwardHolderList = new ArrayList<>();

    private List<UrlMappingRule> ruleList = new ArrayList<>();

    private List<UrlMappingRuleRewriter> ruleRewriterList = new ArrayList<>();

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

    public void addDefaultRequestHandler(String attrute, Object... handlerList) {
        for (Object handler : handlerList) {
            defaultHandlerList.add(new RequestHandlerHolder(attrute, handler));
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
            rule.setAttributeList(new ArrayList<>(copyFromRule.getAttributeList()));
            rule.setExtraVarMap(new HashMap<>(copyFromRule.getExtraVarMap()));
            rule.setHandlerList(new ArrayList<>(copyFromRule.getHandlerList()));
            rule.setInterceptorList(new ArrayList<>(copyFromRule.getInterceptorList()));
            rule.setPriority(copyFromRule.getPriority());
            rule.setResultTransformerList(new ArrayList<>(copyFromRule.getResultTransformerList()));
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
                if (handlerHolder.attribute == null) {
                    requestHandlerList.add(handlerHolder.handler);
                } else if (rule.hasAttribute(handlerHolder.attribute)) {
                    requestHandlerList.add(handlerHolder.handler);
                }
            }
            requestHandlerList.addAll(rule.getHandlerList());
            rule.setHandlerList(requestHandlerList);
        }

        // set global result forward
        for (UrlMappingRule rule : arrangedRuleList) {
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

            // add default transformer
            List<ResultTransformer> transformerList = rule.getResultTransformerList();
            transformerList.add(redirectTransformer);
            transformerList.add(asta4dPageTransformer);

            // move the default forward rule to the last of the result list
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

            if (defaultTransformer != null) {
                transformerList.add(defaultTransformer);
            }

            rule.setResultTransformerList(transformerList);
        }// sortedRuleList loop

        return arrangedRuleList;
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

    public HandyRuleWithRemap add(HttpMethod method, String sourcePath) {
        HandyRuleWithRemap handyRule = new HandyRuleWithRemap(createDefaultRule(method, sourcePath));
        return handyRule;
    }

    public HandyRuleWithAttrOnly add(HttpMethod method, String sourcePath, String targetPath) {
        UrlMappingRule rule = createDefaultRule(method, sourcePath);
        HandyRule handyRule = new HandyRule(rule);
        handyRule.forward(targetPath);
        return new HandyRuleWithAttrOnly(rule);
    }

    public HandyRuleWithRemap add(String sourcePath) {
        return add(defaultMethod, sourcePath);
    }

    public HandyRuleWithAttrOnly add(String sourcePath, String targetPath) {
        return add(defaultMethod, sourcePath, targetPath);
    }

}
