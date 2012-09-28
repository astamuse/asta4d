package com.astamuse.asta4d.web.dispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.annotation.RequestHandler;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingResult;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.view.Asta4dView;
import com.astamuse.asta4d.web.view.RedirectView;
import com.astamuse.asta4d.web.view.WebPageView;

public class RequestDispatcher {

    public final static String KEY_CURRENT_RULE = RequestDispatcher.class.getName() + "##KEY_CURRENT_RULE";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private DispatcherRuleExtractor ruleExtractor;

    private List<UrlMappingRule> ruleList;

    public RequestDispatcher() {

    }

    public DispatcherRuleExtractor getRuleExtractor() {
        return ruleExtractor;
    }

    public void setRuleExtractor(DispatcherRuleExtractor ruleExtractor) {
        this.ruleExtractor = ruleExtractor;
    }

    public List<UrlMappingRule> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<UrlMappingRule> ruleList) {
        this.ruleList = ruleList;
    }

    public Asta4dView handleRequest(HttpServletRequest request) throws Exception {
        // TODO should we handle the exceptions?

        UrlMappingResult result = ruleExtractor.findMappedRule(request, ruleList);
        // TODO if not found result, we should return 404
        WebApplicationContext context = (WebApplicationContext) Context.getCurrentThreadContext();
        context.setData(KEY_CURRENT_RULE, result.getRule());

        writePathVarToContext(context, result.getPathVarMap());
        return invokeHandler(result.getRule().getHandlerList());

    }

    private void writePathVarToContext(WebApplicationContext context, Map<String, String> pathVarMap) {
        Iterator<Entry<String, String>> it = pathVarMap.entrySet().iterator();
        Entry<String, String> entry;
        while (it.hasNext()) {
            entry = it.next();
            context.setData(WebApplicationContext.SCOPE_PATHVAR, entry.getKey(), entry.getValue());
        }
    }

    private Asta4dView invokeHandler(List<Object> handlerList) throws InvocationTargetException, IllegalAccessException,
            DataOperationException {
        // TODO we need a cache here
        Asta4dView view = null;
        for (Object handler : handlerList) {
            if (handler instanceof RequestHandlerAdapter) {
                view = invokeHandler(((RequestHandlerAdapter) handler).asRequestHandler());
            } else {
                view = invokeHandler(handler);
            }
            if (view != null) {
                break;
            }
        }
        return view;
    }

    private Asta4dView invokeHandler(Object handler) throws InvocationTargetException, IllegalAccessException, DataOperationException {
        Method[] methodList = handler.getClass().getMethods();
        Method m = null;
        for (Method method : methodList) {
            if (method.isAnnotationPresent(RequestHandler.class)) {
                m = method;
                break;
            }
        }

        if (m == null) {
            // TODO maybe we can return a null?
            String msg = String.format("Request handler method not found:" + handler.getClass().getName());
            logger.error(msg);
            throw new InvocationTargetException(new RuntimeException(msg));
        }

        Object[] params = InjectUtil.getMethodInjectParams(m);
        if (params == null) {
            params = new Object[0];
        }
        Object result = m.invoke(handler, params);
        if (result == null) {
            return null;
        } else if (result instanceof String) {
            return new WebPageView((String) result);
        } else if (result instanceof RedirectView) {
            return ((RedirectView) result);
        }
        throw new UnsupportedOperationException("Result Type:" + result.getClass().getName());
    }
}
