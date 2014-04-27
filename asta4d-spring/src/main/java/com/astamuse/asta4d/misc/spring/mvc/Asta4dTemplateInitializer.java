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

package com.astamuse.asta4d.misc.spring.mvc;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.WebApplicatoinConfigurationInitializer;

/**
 * 
 * This class is used to initialize necessary Asta4D's configuration and context when Asta4D is used as only the template solution for
 * Spring MVC.
 * 
 * This class should be declared as singeleton in spring container.
 * 
 * @author e-ryu
 * 
 */
public class Asta4dTemplateInitializer extends HandlerInterceptorAdapter implements ServletContextAware, ApplicationContextAware,
        InitializingBean, ServletConfigAware {

    private ServletConfig servletConfig;

    private ServletContext servletContext;

    private ApplicationContext beanContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanContext = applicationContext;
    }

    @Override
    public void setServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        WebApplicationConfiguration asta4dConf = beanContext.getBean(WebApplicationConfiguration.class);
        createConfigurationInitializer().initConfigurationFromFile(servletConfig, asta4dConf);
        WebApplicationConfiguration.setConfiguration(asta4dConf);
    }

    protected WebApplicatoinConfigurationInitializer createConfigurationInitializer() {
        return new WebApplicatoinConfigurationInitializer();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        WebApplicationContext asta4dContext = WebApplicationContext.getCurrentThreadContext();
        if (asta4dContext == null) {
            asta4dContext = new WebApplicationContext();
            Context.setCurrentThreadContext(asta4dContext);
        }
        asta4dContext.init();
        asta4dContext.setRequest(request);
        asta4dContext.setResponse(response);
        asta4dContext.setServletContext(servletContext);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Context asta4dContext = Context.getCurrentThreadContext();
        if (asta4dContext != null) {
            asta4dContext.clear();
        }
        super.afterCompletion(request, response, handler, ex);
    }

}
