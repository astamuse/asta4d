package com.astamuse.asta4d.web.dispatch.mapping.ext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.astamuse.asta4d.web.dispatch.HttpMethod;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerInterceptor;
import com.astamuse.asta4d.web.dispatch.mapping.ResultDescriptor;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.response.provider.Asta4DPageProvider;
import com.astamuse.asta4d.web.util.DeclareInstanceUtil;

public class HandyUrlMappingRule extends UrlMappingRule {

    public final static String REDIRECT_ATTR = HandyUrlMappingRule.class.getName() + "REDIRECT_ATTR";

    public final static int DEFAULT_PRIORITY = 0;

    private final static AtomicInteger Sequencer = new AtomicInteger();

    public HandyUrlMappingRule(HttpMethod method, String sourcePath) {
        super();
        this.setSeq(Sequencer.incrementAndGet());
        this.setMethod(method);
        this.setSourcePath(sourcePath);
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
            list.add(DeclareInstanceUtil.createInstance((handler)));
        }
        return this;
    }

    public HandyUrlMappingRule interceptor(Object... interceptorList) {
        List<RequestHandlerInterceptor> list = this.getInterceptorList();
        for (Object interceptor : interceptorList) {
            list.add((RequestHandlerInterceptor) DeclareInstanceUtil.createInstance(interceptor));
        }
        return this;
    }

    public void redirect() {
        this.attribute(REDIRECT_ATTR);
    }

    public HandyUrlMappingRule forward(Object result, Object contentProvider) {
        this.getContentProviderMap().add(new ResultDescriptor(result, DeclareInstanceUtil.createInstance(contentProvider)));
        return this;
    }

    public HandyUrlMappingRule forward(Object result, String targetPath) {
        return this.forward(result, new Asta4DPageProvider(targetPath));
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
