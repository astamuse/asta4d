package com.astamuse.asta4d.web.dispatch.mapping.ext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.astamuse.asta4d.web.dispatch.HttpMethod;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.ext.builtin.RedirectHandler;
import com.astamuse.asta4d.web.forward.ForwardDescriptor;

public class HandyUrlMappingRule extends UrlMappingRule {

    public final static int DEFAULT_PRIORITY = 0;

    private final static AtomicInteger Sequencer = new AtomicInteger();

    private final static RedirectHandler redirectHandler = new RedirectHandler();

    private UrlMappingRuleHelper helper;

    public HandyUrlMappingRule(UrlMappingRuleHelper helper, HttpMethod method, String sourcePath, String defaultTarget) {
        super(Sequencer.incrementAndGet(), method, sourcePath, defaultTarget, Collections.emptyList(), DEFAULT_PRIORITY, Collections
                .<Class<? extends ForwardDescriptor>, String> emptyMap());
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
        helper.addHandlerListToRule(this, handlerList);
        return this;
    }

    public void redirect() {
        handler(redirectHandler);
    }

    public HandyUrlMappingRule forward(Class<? extends ForwardDescriptor> key, String value) {
        Map<Class<? extends ForwardDescriptor>, String> map = this.getForwardDescriptors();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(key, value);
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

}
