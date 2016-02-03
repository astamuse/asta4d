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

package com.astamuse.asta4d.web;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.interceptor.PageInterceptor;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.web.dispatch.AntPathRuleMatcher;
import com.astamuse.asta4d.web.dispatch.DefaultRequestHandlerInvokerFactory;
import com.astamuse.asta4d.web.dispatch.DispatcherRuleMatcher;
import com.astamuse.asta4d.web.dispatch.RequestHandlerInvokerFactory;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleInitializer;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleSet;
import com.astamuse.asta4d.web.dispatch.mapping.handy.HandyRuleSet;
import com.astamuse.asta4d.web.util.bean.DeclareInstanceResolver;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;
import com.astamuse.asta4d.web.util.message.MessageRenderingHelper;
import com.astamuse.asta4d.web.util.timeout.DefaultSessionAwareExpirableDataManager;
import com.astamuse.asta4d.web.util.timeout.ExpirableDataManager;

public class WebApplicationConfiguration extends Configuration {

    private String flashScopeForwardParameterName = "flash_scope_id";

    private ExpirableDataManager expirableDataManager = new DefaultSessionAwareExpirableDataManager();

    private MessageRenderingHelper messageRenderingHelper = new DefaultMessageRenderingHelper();

    private RequestHandlerInvokerFactory requestHandlerInvokerFactory;

    private List<DeclareInstanceResolver> instanceResolverList = new ArrayList<>();

    private DispatcherRuleMatcher ruleMatcher = new AntPathRuleMatcher();

    private UrlMappingRuleInitializer urlMappingRuleInitializer = null;

    private Class<? extends UrlMappingRuleSet> urlMappingRuleSetCls = HandyRuleSet.class;

    public WebApplicationConfiguration() {
        this.setTemplateResolver(new WebApplicationTemplateResolver());
        this.setContextDataFinder(new WebApplicationContextDataFinder());
        this.setRequestHandlerInvokerFactory(new DefaultRequestHandlerInvokerFactory());
        this.setPageInterceptorList(new LinkedList<PageInterceptor>());

    }

    protected List<PageInterceptor> createDefaultPageInterceptorList() {
        // afford a convenience for global rendering by default
        List<PageInterceptor> pageInterceptorList = new LinkedList<>();
        // configurable message rendering interceptor
        pageInterceptorList.add(new PageInterceptor() {

            @Override
            public void prePageRendering(Renderer renderer) {
                // do nothing
            }

            @Override
            public void postPageRendering(Renderer renderer) {
                MessageRenderingHelper helper = WebApplicationConfiguration.getWebApplicationConfiguration().getMessageRenderingHelper();
                if (helper != null) {
                    renderer.add(helper.createMessageRenderer());
                }
            }
        });
        return pageInterceptorList;
    }

    @Override
    public void setPageInterceptorList(List<PageInterceptor> pageInterceptorList) {
        List<PageInterceptor> list = createDefaultPageInterceptorList();
        list.addAll(pageInterceptorList);
        super.setPageInterceptorList(list);
    }

    public final static WebApplicationConfiguration getWebApplicationConfiguration() {
        return (WebApplicationConfiguration) getConfiguration();
    }

    public String getFlashScopeForwardParameterName() {
        return flashScopeForwardParameterName;
    }

    public void setFlashScopeForwardParameterName(String flashScopeForwardParameterName) {
        this.flashScopeForwardParameterName = flashScopeForwardParameterName;
    }

    public ExpirableDataManager getExpirableDataManager() {
        return expirableDataManager;
    }

    public void setExpirableDataManager(ExpirableDataManager expirableDataManager) {
        this.expirableDataManager = expirableDataManager;
    }

    public MessageRenderingHelper getMessageRenderingHelper() {
        return messageRenderingHelper;
    }

    public void setMessageRenderingHelper(MessageRenderingHelper messageRenderingHelper) {
        this.messageRenderingHelper = messageRenderingHelper;
    }

    public RequestHandlerInvokerFactory getRequestHandlerInvokerFactory() {
        return requestHandlerInvokerFactory;
    }

    public void setRequestHandlerInvokerFactory(RequestHandlerInvokerFactory requestHandlerInvokerFactory) {
        this.requestHandlerInvokerFactory = requestHandlerInvokerFactory;
    }

    public List<DeclareInstanceResolver> getInstanceResolverList() {
        return instanceResolverList;
    }

    public void setInstanceResolverList(List<DeclareInstanceResolver> instanceResolverList) {
        this.instanceResolverList = instanceResolverList;
    }

    public DispatcherRuleMatcher getRuleMatcher() {
        return ruleMatcher;
    }

    public void setRuleMatcher(DispatcherRuleMatcher ruleMatcher) {
        this.ruleMatcher = ruleMatcher;
    }

    public UrlMappingRuleInitializer getUrlMappingRuleInitializer() {
        return urlMappingRuleInitializer;
    }

    public void setUrlMappingRuleInitializer(UrlMappingRuleInitializer urlMappingRuleInitializer) {
        this.urlMappingRuleInitializer = urlMappingRuleInitializer;
    }

    public Class<? extends UrlMappingRuleSet> getUrlMappingRuleSetCls() {
        return urlMappingRuleSetCls;
    }

    public void setUrlMappingRuleHelper(Class<? extends UrlMappingRuleSet> urlMappingRuleSetCls) {
        this.urlMappingRuleSetCls = urlMappingRuleSetCls;
    }

}
