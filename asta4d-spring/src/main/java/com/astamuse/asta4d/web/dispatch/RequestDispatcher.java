package com.astamuse.asta4d.web.dispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;


import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.ContextDataFinder;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.annotation.PathVarRewrite;
import com.astamuse.asta4d.web.dispatch.annotation.RequestHandler;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingResult;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public class RequestDispatcher {

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

    public String handleRequest(HttpServletRequest request) throws Exception {
        // TODO should we handle the exceptions?

        UrlMappingResult result = ruleExtractor.findMappedRule(request, ruleList);
        // TODO if not found result, we should return 404
        WebApplicationContext context = (WebApplicationContext) Context.getCurrentThreadContext();
        UrlMappingRule rule = result.getRule();
        processPathVar(context, result.getPathVarMap(), rule.getPathVarRewritter());
        return invokeHandler(rule.getHandler());

        // return null;

    }

    private void processPathVar(WebApplicationContext context, Map<String, String> pathVarMap, Object pathVarRewritter)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        writePathVarToContext(context, pathVarMap);
        if (pathVarRewritter != null) {
            Method[] methodList = pathVarRewritter.getClass().getMethods();
            for (Method method : methodList) {
                if (isPathVarRewriteMethod(method)) {
                    final Map<String, String> varMap = pathVarMap;
                    // only search values in pathVarMap
                    Object[] params = InjectUtil.getMethodInjectParams(method, new ContextDataFinder() {
                        @Override
                        public Object findDataInContext(Context context, String scope, String name, Class<?> type) {
                            return varMap.get(name);
                        }
                    });
                    if (params == null) {
                        params = new Object[0];
                    }
                    @SuppressWarnings("unchecked")
                    Map<String, String> rewriteMap = (Map<String, String>) method.invoke(pathVarRewritter, params);
                    writePathVarToContext(context, rewriteMap);
                    break;
                }
            }
        }

    }

    private boolean isPathVarRewriteMethod(Method method) {
        boolean is = true;
        if (method.isAnnotationPresent(PathVarRewrite.class) && Map.class.isAssignableFrom(method.getReturnType())) {
            Class<?>[] paramTypes = method.getParameterTypes();
            for (Class<?> clz : paramTypes) {
                if (!clz.equals(String.class)) {
                    is = false;
                    break;
                }
            }
        } else {
            is = false;
        }
        return is;
    }

    private void writePathVarToContext(WebApplicationContext context, Map<String, String> pathVarMap) {
        Iterator<Entry<String, String>> it = pathVarMap.entrySet().iterator();
        Entry<String, String> entry;
        while (it.hasNext()) {
            entry = it.next();
            context.setData(WebApplicationContext.SCOPE_PATHVAR, entry.getKey(), entry.getValue());
        }
    }

    private String invokeHandler(Object handler) throws InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        Method[] methodList = handler.getClass().getMethods();
        Method m = null;
        for (Method method : methodList) {
            if (method.isAnnotationPresent(RequestHandler.class)) {
                m = method;
                break;
            }
        }

        if (m == null) {
            throw new InvocationTargetException(new RuntimeException("Request handler method not found:" + handler.getClass().getName()));
        }

        Object[] params = InjectUtil.getMethodInjectParams(m);
        if (params == null) {
            params = new Object[0];
        }
        Object result = m.invoke(handler, params);
        return result.toString();
    }

}
