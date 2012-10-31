package com.astamuse.asta4d.web.dispatch.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.astamuse.asta4d.web.dispatch.HttpMethod;
import com.astamuse.asta4d.web.forward.ForwardDescriptor;

public class UrlMappingRule {

    private int seq;

    private HttpMethod method;

    private String sourcePath;

    private String defaultTargetPath;

    private List<Object> handlerList = new ArrayList<>();

    private Map<String, Object> extraVarMap = new HashMap<String, Object>();

    private Map<Class<? extends ForwardDescriptor>, String> forwardDescriptors = new HashMap<>();

    private int priority;

    public UrlMappingRule(int seq, HttpMethod method, String sourcePath, String defaultTargetPath, List<Object> handlerList, int priority,
            Map<Class<? extends ForwardDescriptor>, String> forwardDescriptors) {
        super();
        this.seq = seq;
        this.method = method;
        this.sourcePath = sourcePath;
        this.defaultTargetPath = defaultTargetPath;
        this.handlerList.addAll(handlerList);
        this.priority = priority;
        this.forwardDescriptors.putAll(forwardDescriptors);
    }

    public UrlMappingRule() {
        super();
        // do nothing
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

    public String getDefaultTargetPath() {
        return defaultTargetPath;
    }

    public void setDefaultTargetPath(String defaultTargetPath) {
        this.defaultTargetPath = defaultTargetPath;
    }

    public List<Object> getHandlerList() {
        return handlerList;
    }

    public void setHandlerList(List<Object> handlerList) {
        this.handlerList = handlerList;
    }

    public Map<String, Object> getExtraVarMap() {
        return extraVarMap;
    }

    public void setExtraVarMap(Map<String, Object> extraVarMap) {
        this.extraVarMap = extraVarMap;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Map<Class<? extends ForwardDescriptor>, String> getForwardDescriptors() {
        return forwardDescriptors;
    }

    public void setForwardDescriptors(Map<Class<? extends ForwardDescriptor>, String> forwardDescriptors) {
        this.forwardDescriptors = forwardDescriptors;
    }

    public static UrlMappingRule getUnmodifiableRule(UrlMappingRule rule) {
        return new UnmodifiableUrlMappingRule(rule);
    }

    private static class UnmodifiableUrlMappingRule extends UrlMappingRule {

        private UnmodifiableUrlMappingRule(UrlMappingRule rule) {
            super(rule.getSeq(), rule.getMethod(), rule.getSourcePath(), rule.getDefaultTargetPath(), rule.getHandlerList(), rule
                    .getPriority(), rule.getForwardDescriptors());
        }

        @Override
        public void setSeq(int seq) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setMethod(HttpMethod method) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setSourcePath(String sourcePath) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDefaultTargetPath(String defaultTargetPath) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Object> getHandlerList() {
            return Collections.unmodifiableList(super.getHandlerList());
        }

        @Override
        public void setHandlerList(List<Object> handlerList) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<String, Object> getExtraVarMap() {
            return Collections.unmodifiableMap(super.getExtraVarMap());
        }

        @Override
        public void setExtraVarMap(Map<String, Object> extraVarMap) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPriority(int priority) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<Class<? extends ForwardDescriptor>, String> getForwardDescriptors() {
            return Collections.unmodifiableMap(super.getForwardDescriptors());
        }

        @Override
        public void setForwardDescriptors(Map<Class<? extends ForwardDescriptor>, String> forwardDescriptors) {
            throw new UnsupportedOperationException();
        }
    }
}
