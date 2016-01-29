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

package com.astamuse.asta4d.misc.spring.mvc.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.dispatch.RequestDispatcher;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleHelper;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleInitializer;

@Controller
public class GenericController implements ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(GenericController.class);

    private ApplicationContext beanCtx = null;

    private RequestDispatcher dispatcher = new RequestDispatcher();

    private List<UrlMappingRule> ruleList;

    public void init() {
        WebApplicationConfiguration conf = beanCtx.getBean(WebApplicationConfiguration.class);
        WebApplicationConfiguration.setConfiguration(conf);
        UrlMappingRuleHelper helper = new UrlMappingRuleHelper();
        UrlMappingRuleInitializer ruleInitializer = conf.getUrlMappingRuleInitializer();
        ruleInitializer.initUrlMappingRules(helper);
        ruleList = helper.getArrangedRuleList();
        logger.info("url mapping rules are initialized.");
    }

    @RequestMapping(value = "/**")
    public void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        dispatcher.dispatchAndProcess(ruleList);
        /*
        Object contentProvider = dispatcher.handleRequest(request);
        return contentProvider == null ? null : convertSpringView(contentProvider);
        */
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.beanCtx = context;
        // we have to inovke init here because the
        // SpringManagedRequestHandlerResolver need to call application context.
        // And there is no matter that rule list is initialized in multi times,
        // so we do not apply a lock here.
        init();
    }

    /*
        private View convertSpringView(ContentProvider contentProvider) throws TemplateException {
            if (contentProvider instanceof Asta4DPageProvider) {
                return new SpringWebPageView((Asta4DPageProvider) contentProvider);
            } else if (contentProvider instanceof RedirectActionProvider) {
                RedirectActionProvider redirector = (RedirectActionProvider) contentProvider;
                String url = RedirectUtil.setFlashScopeData(redirector.getUrl(), redirector.getFlashScopeData());
                return new org.springframework.web.servlet.view.RedirectView(url);
            }
            throw new UnsupportedOperationException("ContentProvider Type:" + contentProvider.getClass().getName());
        }
    */
}
