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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingResult;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.request.ResultTransformerUtil;
import com.astamuse.asta4d.web.dispatch.response.provider.ContentProvider;
import com.astamuse.asta4d.web.dispatch.response.writer.ContentWriter;
import com.astamuse.asta4d.web.dispatch.response.writer.HeaderWriter;
import com.astamuse.asta4d.web.util.DeclareInstanceUtil;
import com.astamuse.asta4d.web.util.RedirectUtil;

public class RequestDispatcher {

    public final static String KEY_CURRENT_RULE = RequestDispatcher.class.getName() + "##KEY_CURRENT_RULE";

    public final static String KEY_REQUEST_HANDLER_RESULT = "RequestDispatcher##KEY_REQUEST_HANDLER_RESULT";

    private final static Logger logger = LoggerFactory.getLogger(RequestDispatcher.class);

    private final static HeaderWriter headerWriter = new HeaderWriter();

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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void dispatchAndProcess(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("access for:" + request.getRequestURI());
        UrlMappingResult result = ruleExtractor.findMappedRule(request, ruleList);

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

        WebApplicationContext context = (WebApplicationContext) Context.getCurrentThreadContext();
        writePathVarToContext(context, result.getPathVarMap());

        UrlMappingRule rule = result.getRule();
        context.setData(KEY_CURRENT_RULE, rule);
        writePathVarToContext(context, rule.getExtraVarMap());
        retrieveFlashScopeData(request);

        List<ContentProvider<?>> requestResult = handleRequest(rule);
        ContentWriter cw;
        for (ContentProvider<?> cp : requestResult) {
            cw = (ContentWriter<?>) DeclareInstanceUtil.createInstance(cp.getContentWriter());
            cw.writeResponse(rule, response, cp.produce());
        }
    }

    /**
     * 
     * @param request
     * @return ContentProvider
     * @throws Exception
     */
    private List<ContentProvider<?>> handleRequest(UrlMappingRule currentRule) throws Exception {
        // TODO should we handle the exceptions?
        WebApplicationContext context = (WebApplicationContext) Context.getCurrentThreadContext();
        RequestHandlerInvokerFactory factory = ((WebApplicationConfiguration) context.getConfiguration()).getRequestHandlerInvokerFactory();
        RequestHandlerInvoker invoker = factory.getInvoker();

        Object requestHandlerResult;
        try {
            requestHandlerResult = invoker.invoke(currentRule);
        } catch (InvocationTargetException ex) {
            logger.error(currentRule.toString(), ex);
            requestHandlerResult = ex.getTargetException();
        } catch (Exception ex) {
            logger.error(currentRule.toString(), ex);
            requestHandlerResult = ex;
        }

        context.setData(KEY_REQUEST_HANDLER_RESULT, requestHandlerResult);

        List<ContentProvider<?>> cpList = new ArrayList<>();

        if (requestHandlerResult instanceof List) {
            List<?> resultList = (List<?>) requestHandlerResult;
            ContentProvider<?> cp;
            for (Object result : resultList) {
                if (result instanceof ContentProvider<?>) {
                    cpList.add((ContentProvider<?>) result);
                } else {
                    cp = ResultTransformerUtil.transform(requestHandlerResult, currentRule.getResultTransformerList());
                    cpList.add(cp);
                }
            }
        } else {
            ContentProvider<?> cp = ResultTransformerUtil.transform(requestHandlerResult, currentRule.getResultTransformerList());
            cpList.add(cp);
        }
        return cpList;
        /*
                Method m = requestHandlerResult == null ? null : AnnotationMethodHelper.findMethod(requestHandlerResult, ContentProvider.class);
                if (m == null) {
                    List<ResultDescriptor> list = currentRule.getContentProviderMap();
                    Class<?> cls;
                    Object instanceIdentifier;
                    ResultDescriptor matchResult = null;
                    for (ResultDescriptor rd : list) {
                        cls = rd.getResultTypeIdentifier();
                        instanceIdentifier = rd.getResultInstanceIdentifier();
                        if (cls == null && instanceIdentifier == null) {
                            // if there is an exception, it should no be matched to the
                            // default rule.
                            if (requestHandlerResult instanceof Exception) {
                                continue;
                            } else {
                                matchResult = rd;
                                break;
                            }
                        } else if (requestHandlerResult == null) {
                            continue;
                        } else if (cls == null) {
                            if (instanceIdentifier.equals(requestHandlerResult)) {
                                matchResult = rd;
                                break;
                            }
                        } else if (cls.isAssignableFrom(requestHandlerResult.getClass())) {
                            matchResult = rd;
                            break;
                        }
                    }
                    if (matchResult == null) {
                        if (requestHandlerResult instanceof Exception) {
                            throw (Exception) requestHandlerResult;
                        } else {
                            throw new NullPointerException("request result should not be null!!![" + currentRule.toString() + "]");
                        }
                    } else {
                        return matchResult;
                    }
                } else {
                    ContentProvider cp = m.getAnnotation(ContentProvider.class);
                    ContentWriter cw = cp.writer().newInstance();
                    return new ResultDescriptor(requestHandlerResult, requestHandlerResult, cw);
                }
        */
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
