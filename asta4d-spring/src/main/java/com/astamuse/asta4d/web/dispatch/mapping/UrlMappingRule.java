package com.astamuse.asta4d.web.dispatch.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.astamuse.asta4d.web.dispatch.HttpMethod;

public class UrlMappingRule {

    private int seq;

    private HttpMethod method;

    private String sourcePath;

    private String defaultTargetPath;

    private List<Object> handlerList = new ArrayList<>();

    private Map<String, Object> extraVarMap = new HashMap<String, Object>();

    private int priority;

    public UrlMappingRule(int seq, HttpMethod method, String sourcePath, String defaultTargetPath, List<Object> handlerList, int priority) {
        super();
        this.seq = seq;
        this.method = method;
        this.sourcePath = sourcePath;
        this.defaultTargetPath = defaultTargetPath;
        if (handlerList != null) {
            this.handlerList.addAll(handlerList);
        }
        this.priority = priority;
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

}
