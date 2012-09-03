package org.jsoupit.misc.spring.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoupit.template.Context;
import org.jsoupit.web.WebApplicationContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class JsoupitTemplateContextInitializer extends HandlerInterceptorAdapter implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Context jsoupitTemplateContext = Context.getCurrentThreadContext();
        if (jsoupitTemplateContext == null) {
            jsoupitTemplateContext = applicationContext.getBean(WebApplicationContext.class);
            Context.setCurrentThreadContext(jsoupitTemplateContext);
        }
        jsoupitTemplateContext.clearSavedData();
        WebApplicationContext webContext = (WebApplicationContext) jsoupitTemplateContext;
        webContext.setRequest(request);
        webContext.setResponse(response);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Context jsoupitTemplateContext = Context.getCurrentThreadContext();
        if (jsoupitTemplateContext != null) {
            jsoupitTemplateContext.clearSavedData();
        }
        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

}
