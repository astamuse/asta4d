package com.astamuse.asta4d.web.dispatch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.annotation.ContentProvider;
import com.astamuse.asta4d.web.dispatch.mapping.ResultDescriptor;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingResult;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.ext.HandyUrlMappingRule;
import com.astamuse.asta4d.web.dispatch.response.ContentWriter;
import com.astamuse.asta4d.web.dispatch.response.provider.Asta4DPageProvider;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectTargetProvider;
import com.astamuse.asta4d.web.util.AnnotationMethodHelper;
import com.astamuse.asta4d.web.util.RedirectUtil;

public class RequestDispatcher {

    public final static String KEY_CURRENT_RULE = RequestDispatcher.class.getName() + "##KEY_CURRENT_RULE";

    public final static String KEY_REQUEST_HANDLER_RESULT = "RequestDispatcher##KEY_REQUEST_HANDLER_RESULT";

    private final static Logger logger = LoggerFactory.getLogger(RequestDispatcher.class);

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

    public void dispatchAndProcess(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UrlMappingResult result = ruleExtractor.findMappedRule(request, ruleList);
        // TODO if not found result, we should return 404
        WebApplicationContext context = (WebApplicationContext) Context.getCurrentThreadContext();
        writePathVarToContext(context, result.getPathVarMap());

        UrlMappingRule rule = result.getRule();
        context.setData(KEY_CURRENT_RULE, rule);
        writePathVarToContext(context, rule.getExtraVarMap());
        retrieveFlashScopeData(request);

        Object contentProvider = handleRequest(rule);

        if (contentProvider == null) {
            // TODO what???
        } else {
            // TODO it is so dirty!!!!!!!
            if (rule.getAttributeList().contains(HandyUrlMappingRule.REDIRECT_ATTR) && contentProvider instanceof Asta4DPageProvider) {
                Asta4DPageProvider a4p = (Asta4DPageProvider) contentProvider;
                contentProvider = new RedirectTargetProvider(a4p.getPath());
            }
            Method m = AnnotationMethodHelper.findMethod(contentProvider, ContentProvider.class);
            Object[] params = InjectUtil.getMethodInjectParams(m);
            if (params == null) {
                params = new Object[0];
            }
            Object content;
            try {
                // TODO cache!!!
                content = m.invoke(contentProvider, params);
                ContentWriter cw = rule.getContentWriter();
                if (cw == null) {
                    ContentProvider cpDef = m.getAnnotation(ContentProvider.class);
                    Class<? extends ContentWriter> cwCls = cpDef.writer();
                    cw = cwCls.newInstance();
                }
                // we suppose that the content writer will deal with null well.
                cw.writeResponse(response, content);
            } catch (InvocationTargetException e) {
                throw e;
            }
        }
    }

    /**
     * 
     * @param request
     * @return ContentProvider
     * @throws Exception
     */
    private Object handleRequest(UrlMappingRule currentRule) throws Exception {
        // TODO should we handle the exceptions?
        WebApplicationContext context = (WebApplicationContext) Context.getCurrentThreadContext();
        RequestHandlerInvokerFactory factory = ((WebApplicationConfiguration) context.getConfiguration()).getRequestHandlerInvokerFactory();
        RequestHandlerInvoker invoker = factory.getInvoker();

        Object requestHandlerResult;
        try {
            requestHandlerResult = invoker.invoke(currentRule);
        } catch (Exception ex) {
            logger.error(currentRule.toString(), ex);
            requestHandlerResult = ex;
        }

        context.setData(KEY_REQUEST_HANDLER_RESULT, requestHandlerResult);

        // determine whether it is a contentprovider
        Method m = requestHandlerResult == null ? null : AnnotationMethodHelper.findMethod(requestHandlerResult, ContentProvider.class);
        if (m == null) {
            List<ResultDescriptor> list = currentRule.getContentProviderMap();
            Object contentProvider = null;
            Class<?> cls;
            Object instanceIdentifier;
            for (ResultDescriptor rd : list) {
                cls = rd.getResultTypeIdentifier();
                instanceIdentifier = rd.getResultInstanceIdentifier();
                if (cls == null && instanceIdentifier == null) {
                    contentProvider = rd.getContentProvider();
                    break;
                } else if (cls == null) {
                    if (instanceIdentifier.equals(requestHandlerResult)) {
                        contentProvider = rd.getContentProvider();
                        break;
                    }
                } else if (cls.isAssignableFrom(requestHandlerResult.getClass())) {
                    contentProvider = rd.getContentProvider();
                    break;
                }
            }
            return contentProvider;
        } else {
            return requestHandlerResult;
        }

    }

    private void writePathVarToContext(WebApplicationContext context, Map<String, Object> pathVarMap) {
        Iterator<Entry<String, Object>> it = pathVarMap.entrySet().iterator();
        Entry<String, Object> entry;
        while (it.hasNext()) {
            entry = it.next();
            context.setData(WebApplicationContext.SCOPE_PATHVAR, entry.getKey(), entry.getValue());
        }
    }

    private void retrieveFlashScopeData(HttpServletRequest request) {
        String flashScopeId = request.getParameter(RedirectUtil.KEY_FLASH_SCOPE_ID);
        if (StringUtils.isEmpty(flashScopeId)) {
            return;
        }
        RedirectUtil.getFlashScopeData(flashScopeId);
    }

}
