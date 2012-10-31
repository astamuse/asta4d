package com.astamuse.asta4d.web.dispatch;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingResult;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.util.RedirectUtil;
import com.astamuse.asta4d.web.view.Asta4dView;
import com.astamuse.asta4d.web.view.WebPageView;

public class RequestDispatcher {

    public final static String KEY_CURRENT_RULE = RequestDispatcher.class.getName() + "##KEY_CURRENT_RULE";

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
        writePathVarToContext(context, result.getPathVarMap());

        UrlMappingRule rule = result.getRule();
        context.setData(KEY_CURRENT_RULE, rule);
        writePathVarToContext(context, rule.getExtraVarMap());
        retrieveFlashScopeData(request);

        RequestHandlerInvokerFactory factory = ((WebApplicationConfiguration) context.getConfiguration()).getRequestHandlerInvokerFactory();

        Asta4dView view = factory.getInvoker().invoke(rule);
        if (view != null) {
            return view;
        }
        return new WebPageView(rule.getDefaultTargetPath());

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
