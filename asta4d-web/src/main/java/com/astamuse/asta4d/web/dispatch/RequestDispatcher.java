/*
 * Copyright 2012 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.astamuse.asta4d.web.dispatch;

import static com.astamuse.asta4d.web.WebApplicationContext.SCOPE_FLASH;

import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingResult;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.response.provider.ContentProvider;
import com.astamuse.asta4d.web.util.redirect.RedirectUtil;

public class RequestDispatcher {

    public final static String KEY_CURRENT_RULE = RequestDispatcher.class.getName() + "##KEY_CURRENT_RULE";

    public final static String KEY_REQUEST_HANDLER_RESULT = "RequestDispatcher##KEY_REQUEST_HANDLER_RESULT";

    private final static Logger logger = LoggerFactory.getLogger(RequestDispatcher.class);

    public RequestDispatcher() {

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void dispatchAndProcess(List<UrlMappingRule> ruleList) throws Exception {
        WebApplicationConfiguration conf = WebApplicationConfiguration.getWebApplicationConfiguration();
        WebApplicationContext context = (WebApplicationContext) Context.getCurrentThreadContext();
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        HttpMethod method = HttpMethod.valueOf(request.getMethod().toUpperCase());
        String uri = context.getAccessURI();
        if (uri == null) {
            uri = URLDecoder.decode(request.getRequestURI(), "UTF-8");
            String contextPath = request.getContextPath();
            uri = uri.substring(contextPath.length());
            context.setAccessURI(uri);
        }

        String queryString = request.getQueryString();

        UrlMappingResult result = conf.getRuleExtractor().findMappedRule(ruleList, method, uri, queryString);

        // if not found result, we do not need return 404, instead of user
        // defining all match rule

        if (result == null) {
            logger.warn("There is no matched rule found, we will simply return a 404. You should define your own matching all rule for this case.");
            response.setStatus(404);
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("apply rule at :" + result.getRule());
        }

        writePathVarToContext(context, result.getPathVarMap());

        UrlMappingRule rule = result.getRule();
        context.setData(KEY_CURRENT_RULE, rule);
        writePathVarToContext(context, rule.getExtraVarMap());
        restoreFlashScopeData(context, request);

        List<ContentProvider> requestResult = handleRequest(rule);
        for (ContentProvider cp : requestResult) {
            cp.produce(rule, response);
        }
    }

    /**
     * 
     * @param request
     * @return ContentProvider
     * @throws Exception
     */
    private List<ContentProvider> handleRequest(UrlMappingRule currentRule) throws Exception {
        Context context = Context.getCurrentThreadContext();
        RequestHandlerInvokerFactory factory = WebApplicationConfiguration.getWebApplicationConfiguration()
                .getRequestHandlerInvokerFactory();
        RequestHandlerInvoker invoker = factory.getInvoker();

        List<ContentProvider> cpList = invoker.invoke(currentRule);
        context.setData(KEY_REQUEST_HANDLER_RESULT, cpList);

        return cpList;

    }

    private void writePathVarToContext(WebApplicationContext context, Map<String, Object> pathVarMap) {
        Iterator<Entry<String, Object>> it = pathVarMap.entrySet().iterator();
        Entry<String, Object> entry;
        while (it.hasNext()) {
            entry = it.next();
            context.setData(WebApplicationContext.SCOPE_PATHVAR, entry.getKey(), entry.getValue());
        }
    }

    private void restoreFlashScopeData(WebApplicationContext context, HttpServletRequest request) {
        Map<String, Object> flashScopeData = RedirectUtil.retrieveFlashScopeData(request);
        if (flashScopeData != null) {
            for (Entry<String, Object> entry : flashScopeData.entrySet()) {
                context.setData(SCOPE_FLASH, entry.getKey(), entry.getValue());
            }
        }
    }

}
