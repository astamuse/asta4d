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

package com.astamuse.asta4d.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.util.concurrent.ListExecutorServiceUtil;
import com.astamuse.asta4d.util.concurrent.SnippetExecutorServiceUtil;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.WebApplicatoinConfigurationInitializer;
import com.astamuse.asta4d.web.dispatch.RequestDispatcher;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleSet;

/**
 * Here we are going to implement a view first mechanism of view resolving. We need a url mapping algorithm too.
 * 
 */
public class Asta4dServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final static Logger logger = LoggerFactory.getLogger(Asta4dServlet.class);

    protected RequestDispatcher dispatcher = new RequestDispatcher();

    private List<UrlMappingRule> ruleList;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            WebApplicationConfiguration asta4dConf = createConfiguration();
            createConfigurationInitializer().initConfigurationFromFile(config, asta4dConf);
            Configuration.setConfiguration(asta4dConf);
            WebApplicationConfiguration.getWebApplicationConfiguration().getExpirableDataManager().start();
            ruleList = createRuleList();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private List<UrlMappingRule> createRuleList() throws InstantiationException, IllegalAccessException {
        WebApplicationConfiguration conf = WebApplicationConfiguration.getWebApplicationConfiguration();
        UrlMappingRuleSet ruleSet = conf.getUrlMappingRuleSetCls().newInstance();
        conf.getUrlMappingRuleInitializer().initUrlMappingRules(ruleSet);
        logger.info("url mapping rules are initialized.");
        return ruleSet.getArrangedRuleList();
    }

    @Override
    protected final void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        logger.debug("access for:{}", req.getRequestURI());
        WebApplicationContext context = null;
        try {
            context = Context.getCurrentThreadContext();
            if (context == null) {
                context = createAsta4dContext();
                Context.setCurrentThreadContext(context);
            }
            context.init();
            context.setRequest(req);
            context.setResponse(res);
            context.setServletContext(getServletContext());

            service();

        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            if (context != null) {
                context.clear();
            }
        }
    }

    /**
     * Subclass can override this method to do something before or after real service process.
     * 
     * eg. {@link WebApplicationContext#setAccessURI(String)} can be called before this method to rewrite access uri.
     * 
     * @throws Exception
     */
    protected void service() throws Exception {
        if (Configuration.getConfiguration().isCacheEnable()) {
            dispatcher.dispatchAndProcess(ruleList);
        } else {
            dispatcher.dispatchAndProcess(createRuleList());
        }
    }

    protected WebApplicationContext createAsta4dContext() {
        WebApplicationContext context = new WebApplicationContext();
        return context;
    }

    protected WebApplicationConfiguration createConfiguration() {
        return new WebApplicationConfiguration();
    }

    protected WebApplicatoinConfigurationInitializer createConfigurationInitializer() {
        return new WebApplicatoinConfigurationInitializer();
    }

    @Override
    public void destroy() {
        // TODO we want a more elegant way to release the threads rather than shutdown them explicitly
        ListExecutorServiceUtil.getExecutorService().shutdownNow();
        SnippetExecutorServiceUtil.getExecutorService().shutdownNow();
        WebApplicationConfiguration.getWebApplicationConfiguration().getExpirableDataManager().stop();
        WebApplicationConfiguration.setConfiguration(null);// for gc
        System.gc();// if possible
        super.destroy();
    }

}
