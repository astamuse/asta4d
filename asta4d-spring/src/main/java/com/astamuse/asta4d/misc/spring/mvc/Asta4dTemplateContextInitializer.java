package com.astamuse.asta4d.misc.spring.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationContext;

public class Asta4dTemplateContextInitializer extends HandlerInterceptorAdapter implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Context templateContext = Context.getCurrentThreadContext();
        if (templateContext == null) {
            templateContext = applicationContext.getBean(WebApplicationContext.class);
            Context.setCurrentThreadContext(templateContext);
        }
        templateContext.clearSavedData();
        WebApplicationContext webContext = (WebApplicationContext) templateContext;
        webContext.setRequest(request);
        webContext.setResponse(response);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Context templateContext = Context.getCurrentThreadContext();
        if (templateContext != null) {
            templateContext.clearSavedData();
        }
        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

}
