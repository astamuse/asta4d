package com.astamuse.asta4d.misc.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.servlet.Asta4dServlet;

public class SpringInitializableServlet extends Asta4dServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected WebApplicationConfiguration createConfiguration() {
        String configLocation = getServletConfig().getInitParameter("contextConfigLocation");
        if (configLocation.startsWith("classpath:")) {
            configLocation = configLocation.substring("classpath:".length());
        }
        if (!configLocation.startsWith("/")) {
            configLocation = "/" + configLocation;
        }
        ApplicationContext springContext = new ClassPathXmlApplicationContext(configLocation);
        return springContext.getBean(WebApplicationConfiguration.class);
    }

}
