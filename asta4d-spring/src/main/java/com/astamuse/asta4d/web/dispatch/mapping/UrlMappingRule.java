package com.astamuse.asta4d.web.dispatch.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.astamuse.asta4d.web.dispatch.HttpMethod;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerInterceptor;

public class UrlMappingRule {

    private int seq;

    private HttpMethod method;

    private String sourcePath;

    private List<Object> handlerList;

    private List<RequestHandlerInterceptor> interceptorList;

    private Map<String, Object> extraVarMap;

    private List<String> attributeList;

    private List<ResultDescriptor> contentProviderMap;

    private int priority;

    private UrlMappingRule unModifiableDelegator;

    public UrlMappingRule(int seq, HttpMethod method, String sourcePath, List<Object> handlerList,
            List<RequestHandlerInterceptor> interceptorList, Map<String, Object> extraVarMap, List<String> attributeList,
            List<ResultDescriptor> contentProviderMap, int priority) {
        super();
        this.seq = seq;
        this.method = method;
        this.sourcePath = sourcePath;
        this.handlerList = handlerList;
        this.interceptorList = interceptorList;
        this.extraVarMap = extraVarMap;
        this.attributeList = attributeList;
        this.contentProviderMap = contentProviderMap;
        this.priority = priority;
    }

    public UrlMappingRule() {
        super();
        this.handlerList = new ArrayList<>();
        this.interceptorList = new ArrayList<>();
        this.extraVarMap = new HashMap<>();
        this.attributeList = new ArrayList<>();
        this.contentProviderMap = new ArrayList<>();
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

    public List<ResultDescriptor> getContentProviderMap() {
        return contentProviderMap;
    }

    public void setContentProviderMap(List<ResultDescriptor> contentProviderMap) {
        this.contentProviderMap = contentProviderMap;
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
                ", contentProviderMap=" + contentProviderMap + ", priority=" + priority + "]";
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
            return rule.getExtraVarMap();
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

        public List<ResultDescriptor> getContentProviderMap() {
            return Collections.unmodifiableList(rule.getContentProviderMap());
        }

        public void setContentProviderMap(List<ResultDescriptor> contentProviderMap) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return "READONLY:" + rule.toString();
        }
    }

}
