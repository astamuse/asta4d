package com.astamuse.asta4d.web.dispatch.mapping.ext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.astamuse.asta4d.web.dispatch.HttpMethod;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.ext.builtin.DefaultHandlerBuilder;
import com.astamuse.asta4d.web.dispatch.mapping.ext.builtin.DirectRenderingHandler;

public class UrlMappingRuleHelper {

    private DirectRenderingHandler defaultHandler = new DirectRenderingHandler();

    private HttpMethod defaultMethod = HttpMethod.GET;

    private List<RequestHandlerBuilder> requestHandlerBuilderList = new ArrayList<>();

    private RequestHandlerBuilder defaultBuilder = new DefaultHandlerBuilder();

    private List<UrlMappingRule> urlRules = new ArrayList<>();

    public void setDefaultMethod(HttpMethod defaultMethod) {
        this.defaultMethod = defaultMethod;
    }

    public void addRequestHandlerBuilder(RequestHandlerBuilder builder) {
        requestHandlerBuilderList.add(builder);
    }

    public List<UrlMappingRule> getSortedRuleList() {
        List<UrlMappingRule> sortedRuleList = new ArrayList<>(urlRules);
        Collections.sort(sortedRuleList, new Comparator<UrlMappingRule>() {
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
        List<Object> handlerList;
        for (UrlMappingRule rule : sortedRuleList) {
            handlerList = rule.getHandlerList();
            if (handlerList == null) {
                handlerList = new ArrayList<>();
            }
            handlerList.add(defaultHandler);
            rule.setHandlerList(handlerList);
        }
        return sortedRuleList;
    }

    public HandyUrlMappingRule add(HttpMethod method, String sourcePath, String targetPath, Object... handlerList) {
        HandyUrlMappingRule rule = new HandyUrlMappingRule(this, method, sourcePath, targetPath);
        addHandlerListToRule(rule, handlerList);
        urlRules.add(rule);
        return rule;
    }

    public void addHandlerListToRule(HandyUrlMappingRule rule, Object... handlerList) {
        List<Object> list = rule.getHandlerList();
        if (list == null) {
            list = new ArrayList<>();
        }
        for (Object handler : handlerList) {
            list.add(buildHandler(handler));
        }
        rule.setHandlerList(list);
    }

    public Object buildHandler(Object handlerDeclaration) {
        Object handler = null;
        for (RequestHandlerBuilder builder : requestHandlerBuilderList) {
            handler = builder.createRequestHandler(handlerDeclaration);
            if (handler != null) {
                break;
            }
        }
        if (handler == null) {
            handler = defaultBuilder.createRequestHandler(handlerDeclaration);
        }
        return handler;
    }

    public HandyUrlMappingRule add(HttpMethod method, String sourcePath, Object... handlerList) {
        return add(method, sourcePath, null, handlerList);
    }

    public HandyUrlMappingRule add(String sourcePath, Object... handlerList) {
        return add(defaultMethod, sourcePath, handlerList);
    }

    public HandyUrlMappingRule add(String sourcePath, String targetPath, Object... handlerList) {
        return add(defaultMethod, sourcePath, targetPath, handlerList);
    }

}
