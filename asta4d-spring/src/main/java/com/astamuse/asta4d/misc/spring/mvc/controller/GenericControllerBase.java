package com.astamuse.asta4d.misc.spring.mvc.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.astamuse.asta4d.web.dispatch.RequestDispatcher;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleHelper;

//TODO need to cache the mapped result
@Controller
public abstract class GenericControllerBase implements ApplicationContextAware {

    protected ApplicationContext beanCtx;

    private RequestDispatcher dispatcher = new RequestDispatcher();

    private void init() {
        UrlMappingRuleHelper rules = new UrlMappingRuleHelper();
        initUrlMappingRules(rules);
        dispatcher.setRuleExtractor(new AntPathRuleExtractor());
        dispatcher.setRuleList(rules.getSortedRuleList());
    }

    @RequestMapping(value = "/**")
    public String doService(HttpServletRequest request) throws Exception {
        return dispatcher.handleRequest(request);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.beanCtx = context;
        // we have to inovke init here because the child class would want to
        // call application context. And there is no matter that dispatcher is
        // initialized in multi times, so we do not apply a lock here.
        init();
    }

    protected abstract void initUrlMappingRules(UrlMappingRuleHelper rules);
}
