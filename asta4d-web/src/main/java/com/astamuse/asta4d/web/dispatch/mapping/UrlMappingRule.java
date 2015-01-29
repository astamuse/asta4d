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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.astamuse.asta4d.web.dispatch.DispatcherRuleMatcher;
import com.astamuse.asta4d.web.dispatch.HttpMethod;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerInterceptor;
import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;

public class UrlMappingRule {

    private int seq;

    private HttpMethod method;

    private String sourcePath;

    private List<Object> handlerList;

    private List<RequestHandlerInterceptor> interceptorList;

    private Map<String, Object> extraVarMap;

    private List<String> attributeList;

    private List<ResultTransformer> resultTransformerList;

    private int priority;

    private DispatcherRuleMatcher ruleMatcher;

    private UrlMappingRule unModifiableDelegator;

    public UrlMappingRule(int seq, HttpMethod method, String sourcePath, List<Object> handlerList,
            List<RequestHandlerInterceptor> interceptorList, Map<String, Object> extraVarMap, List<String> attributeList,
            List<ResultTransformer> resultTransformerList, int priority, DispatcherRuleMatcher ruleMatcher) {
        super();
        this.seq = seq;
        this.method = method;
        this.sourcePath = sourcePath;
        this.handlerList = handlerList;
        this.interceptorList = interceptorList;
        this.extraVarMap = extraVarMap;
        this.attributeList = attributeList;
        this.resultTransformerList = resultTransformerList;
        this.priority = priority;
        this.ruleMatcher = ruleMatcher;
    }

    public UrlMappingRule() {
        super();
        this.handlerList = new ArrayList<>();
        this.interceptorList = new ArrayList<>();
        this.extraVarMap = new HashMap<>();
        this.attributeList = new ArrayList<>();
        this.resultTransformerList = new ArrayList<>();
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public List<Object> getHandlerList() {
        return handlerList;
    }

    public void setHandlerList(List<Object> handlerList) {
        this.handlerList = handlerList;
    }

    public List<RequestHandlerInterceptor> getInterceptorList() {
        return interceptorList;
    }

    public void setInterceptorList(List<RequestHandlerInterceptor> interceptorList) {
        this.interceptorList = interceptorList;
    }

    public Map<String, Object> getExtraVarMap() {
        return extraVarMap;
    }

    public void setExtraVarMap(Map<String, Object> extraVarMap) {
        this.extraVarMap = extraVarMap;
    }

    public Object extraVar(String key) {
        return this.extraVarMap.get(key);
    }

    public List<String> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<String> attributeList) {
        this.attributeList = attributeList;
    }

    public boolean hasAttribute(String attr) {
        return this.attributeList.contains(attr);
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public DispatcherRuleMatcher getRuleMatcher() {
        return ruleMatcher;
    }

    public void setRuleMatcher(DispatcherRuleMatcher ruleMatcher) {
        this.ruleMatcher = ruleMatcher;
    }

    public List<ResultTransformer> getResultTransformerList() {
        return resultTransformerList;
    }

    public void setResultTransformerList(List<ResultTransformer> resultTransformerList) {
        this.resultTransformerList = resultTransformerList;
    }

    public UrlMappingRule asUnmodifiable() {
        // It is OK if unModifiableDelegator was initialized by multiple threads
        if (unModifiableDelegator == null) {
            unModifiableDelegator = new UnModifiableUrlMappingRule(this);
        }
        return unModifiableDelegator;
    }

    @Override
    public String toString() {
        return "UrlMappingRule [seq=" + seq + ", method=" + method + ", sourcePath=" + sourcePath + ", handlerList=" + handlerList +
                ", interceptorList=" + interceptorList + ", extraVarMap=" + extraVarMap + ", attributeList=" + attributeList +
                ", resultTransformerList=" + resultTransformerList + ", priority=" + priority + ", ruleMatcher=" + ruleMatcher + "]";
    }

    private static class UnModifiableUrlMappingRule extends UrlMappingRule {
        private UrlMappingRule rule;

        private UnModifiableUrlMappingRule(UrlMappingRule rule) {
            this.rule = rule;
        }

        public int getSeq() {
            return rule.getSeq();
        }

        public void setSeq(int seq) {
            throw new UnsupportedOperationException();
        }

        public HttpMethod getMethod() {
            return rule.getMethod();
        }

        public void setMethod(HttpMethod method) {
            throw new UnsupportedOperationException();
        }

        public String getSourcePath() {
            return rule.getSourcePath();
        }

        public void setSourcePath(String sourcePath) {
            throw new UnsupportedOperationException();
        }

        public List<Object> getHandlerList() {
            return Collections.unmodifiableList(rule.getHandlerList());
        }

        public void setHandlerList(List<Object> handlerList) {
            throw new UnsupportedOperationException();
        }

        public List<RequestHandlerInterceptor> getInterceptorList() {
            return Collections.unmodifiableList(rule.getInterceptorList());
        }

        public void setInterceptorList(List<RequestHandlerInterceptor> interceptorList) {
            throw new UnsupportedOperationException();
        }

        public Map<String, Object> getExtraVarMap() {
            return Collections.unmodifiableMap(rule.getExtraVarMap());
        }

        public void setExtraVarMap(Map<String, Object> extraVarMap) {
            throw new UnsupportedOperationException();
        }

        public Object extraVar(String key) {
            return rule.extraVar(key);
        }

        public List<String> getAttributeList() {
            return Collections.unmodifiableList(rule.getAttributeList());
        }

        public void setAttributeList(List<String> attributeList) {
            throw new UnsupportedOperationException();
        }

        public boolean hasAttribute(String attr) {
            return rule.hasAttribute(attr);
        }

        public int getPriority() {
            return rule.getPriority();
        }

        public void setPriority(int priority) {
            throw new UnsupportedOperationException();
        }

        public DispatcherRuleMatcher getRuleMatcher() {
            return rule.getRuleMatcher();
        }

        public void setRuleMatcher(DispatcherRuleMatcher ruleMatcher) {
            throw new UnsupportedOperationException();
        }

        public List<ResultTransformer> getResultTransformerList() {
            return Collections.unmodifiableList(rule.getResultTransformerList());
        }

        public void setResultTransformerList(List<ResultTransformer> resultTransformerList) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return "READONLY:" + rule.toString();
        }
    }

}
