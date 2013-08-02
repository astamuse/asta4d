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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.RequestDispatcher;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.ext.UrlMappingRuleHelper;
import com.astamuse.asta4d.web.util.SystemPropertyUtil;
import com.astamuse.asta4d.web.util.SystemPropertyUtil.PropertyScope;

/**
 * Here we are going to implement a view first mechanism of view resolving. We
 * need a url mapping algorithm too.
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
            initConfigurationFromFile(config, asta4dConf);
            Configuration.setConfiguration(asta4dConf);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        ruleList = createRuleList();
    }

    private List<UrlMappingRule> createRuleList() {
        UrlMappingRuleHelper helper = new UrlMappingRuleHelper();
        WebApplicationConfiguration conf = WebApplicationConfiguration.getWebApplicationConfiguration();
        conf.getUrlMappingRuleInitializer().initUrlMappingRules(helper);
        logger.info("url mapping rules are initialized.");
        return helper.getArrangedRuleList();
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
     * Subclass can override this method to do something before or after real
     * service process.
     * 
     * eg. {@link WebApplicationContext#setAccessURI(String)} can be called
     * before this method to rewrite access uri.
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

    protected void initConfigurationFromFile(ServletConfig sc, WebApplicationConfiguration conf) throws Exception {
        String[] fileNames = retrievePossibleConfigurationFileNames();
        InputStream input = null;
        String fileType = null;

        // find file from classpath
        ClassLoader clsLoder = this.getClass().getClassLoader();
        for (String name : fileNames) {
            input = clsLoder.getResourceAsStream(name);
            if (input != null) {
                fileType = FilenameUtils.getExtension(name);
                break;
            }
        }

        // find from file system
        // I can do goto by while loop :)
        while (input == null) {

            // find key
            String fileKey = retrieveConfigurationFileNameKey();
            if (fileKey == null) {
                break;
            }

            // get path
            String filePath = retrieveConfigurationFileName(sc, fileKey);
            if (filePath == null) {
                break;
            }

            // load file
            File f = new File(filePath);
            input = new FileInputStream(f);
            fileType = FilenameUtils.getExtension(filePath);
            break;
        }

        if (input != null) {
            try {
                switch (fileType) {
                case "properties":
                    Properties ps = new Properties();
                    ps.load(input);
                    // PropertyUtils.setProperty(bean, name, value)
                    Enumeration<Object> keys = ps.keys();
                    while (keys.hasMoreElements()) {
                        String key = keys.nextElement().toString();
                        BeanUtils.setProperty(conf, key, ps.get(key));
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("File type of " + fileType +
                            " does not be supported for initialize asta4d Configuration.");
                }
            } finally {
                input.close();
            }
        }
    }

    protected String[] retrievePossibleConfigurationFileNames() {
        // return new String[] {
        // "asta4d.conf.properties, ast4d.conf.js, asta4d.conf.groovy" };
        return new String[] { "asta4d.conf.properties" };
    }

    protected String retrieveConfigurationFileNameKey() {
        return "asta4d.conf";
    }

    protected String retrieveConfigurationFileName(ServletConfig sc, String key) {
        return SystemPropertyUtil.retrievePropertyValue(sc, key, PropertyScope.ServletConfig, PropertyScope.JNDI,
                PropertyScope.SystemProperty);
    }
}
