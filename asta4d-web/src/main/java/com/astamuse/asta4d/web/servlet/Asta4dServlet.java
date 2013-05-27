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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.RequestDispatcher;
import com.astamuse.asta4d.web.dispatch.mapping.ext.UrlMappingRuleHelper;

/**
 * Here we are going to implement a view first mechanism of view resolving. We
 * need a url mapping algorithm too.
 * 
 */
public abstract class Asta4dServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final static Logger logger = LoggerFactory.getLogger(Asta4dServlet.class);

    protected RequestDispatcher dispatcher = new RequestDispatcher();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        UrlMappingRuleHelper helper = new UrlMappingRuleHelper();
        initUrlMappingRules(helper);
        dispatcher.setRuleExtractor(createConfiguration().getRuleExtractor());
        dispatcher.setRuleList(helper.getArrangedRuleList());
        logger.info("url mapping rules are initialized.");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Context context = null;
        try {
            context = Context.getCurrentThreadContext();
            if (context == null) {
                context = createAsta4dContext();
                Context.setCurrentThreadContext(context);
            }
            dispatcher.dispatchAndProcess(req, res);
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            if (context != null) {
                context.clear();
            }
        }
    }

    protected WebApplicationContext createAsta4dContext() {
        WebApplicationContext context = new WebApplicationContext();
        context.setConfiguration(createConfiguration());
        return context;
    }

    protected WebApplicationConfiguration createConfiguration() {
        return new WebApplicationConfiguration();
    }

    protected abstract void initUrlMappingRules(UrlMappingRuleHelper rules);

}
