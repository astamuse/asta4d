package com.astamuse.asta4d.web.dispatch.mapping.ext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.astamuse.asta4d.web.dispatch.HttpMethod;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerInterceptor;
import com.astamuse.asta4d.web.dispatch.mapping.DefaultForwardDescriptor;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.response.forward.ForwardDescriptor;

public class HandyUrlMappingRule extends UrlMappingRule {

    public final static int DEFAULT_PRIORITY = 0;

    private final static AtomicInteger Sequencer = new AtomicInteger();

    private UrlMappingRuleHelper helper;

    public HandyUrlMappingRule(UrlMappingRuleHelper helper, HttpMethod method, String sourcePath, String defaultTarget) {
        super();
        this.setSeq(Sequencer.incrementAndGet());
        this.setMethod(method);
        this.setSourcePath(sourcePath);
        this.forward(DefaultForwardDescriptor.class, defaultTarget);
        this.helper = helper;
    }

    public HandyUrlMappingRule method(HttpMethod method) {
        this.setMethod(method);
        return this;
    }

    public HandyUrlMappingRule priority(int priority) {
        this.setPriority(priority);
        return this;
    }

    public HandyUrlMappingRule handler(Object... handlerList) {
        List<Object> list = this.getHandlerList();
        for (Object handler : handlerList) {
            list.add(helper.createHandler(handler));
        }
        return this;
    }

    public HandyUrlMappingRule interceptor(Object... interceptorList) {
        List<RequestHandlerInterceptor> list = this.getInterceptorList();
        for (Object interceptor : interceptorList) {
            list.add((RequestHandlerInterceptor) helper.createHandler(interceptor));
        }
        return this;
    }

    public void redirect() {
        this.setForwardActionType(ForwardActionType.Redirect);
    }

    public HandyUrlMappingRule forward(Class<? extends ForwardDescriptor> forwardDescriptor, String path) {
        Map<Class<? extends ForwardDescriptor>, String> map = this.getForwardDescriptorMap();
        map.put(forwardDescriptor, path);
        return this;
    }

    public HandyUrlMappingRule var(String key, Object value) {
        Map<String, Object> map = this.getExtraVarMap();
        if (map == null) {
            map = new HashMap<String, Object>();
        }
        map.put(key, value);
        this.setExtraVarMap(map);
        return this;
    }

    public HandyUrlMappingRule attribute(String attribute) {
        List<String> attrList = this.getAttributeList();
        attrList.add(attribute);
        return this;
    }

}
