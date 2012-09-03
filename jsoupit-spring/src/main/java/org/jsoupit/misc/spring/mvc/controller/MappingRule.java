package org.jsoupit.misc.spring.mvc.controller;

import org.springframework.web.bind.annotation.RequestMethod;

public class MappingRule {

    private int seq;

    private RequestMethod method;

    private String sourceUrl;

    private int priority;

    private Object pathVarRewritter;

    private Object controller;

    public MappingRule(int seq, RequestMethod method, String sourceUrl, int priority, Object pathVarRewritter, Object controller) {
        super();
        this.seq = seq;
        this.method = method;
        this.sourceUrl = sourceUrl;
        this.priority = priority;
        this.pathVarRewritter = pathVarRewritter;
        this.controller = controller;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public void setMethod(RequestMethod method) {
        this.method = method;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Object getPathVarRewritter() {
        return pathVarRewritter;
    }

    public void setPathVarRewritter(Object pathVarRewritter) {
        this.pathVarRewritter = pathVarRewritter;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

}
