package com.astamuse.asta4d.misc.spring.mvc.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.astamuse.asta4d.web.dispatch.RequestDispatcher;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleHelper;

//TODO need to cache the mapped result
@Controller
public abstract class GenericControllerBase {

    private UrlMappingRuleHelper rules = new UrlMappingRuleHelper();

    private RequestDispatcher dispatcher = new RequestDispatcher();

    public GenericControllerBase() {
        super();
        initRules(rules);
        dispatcher.setRuleExtractor(new AntPathRuleExtractor());
        dispatcher.setRuleList(rules.getSortedRuleList());
    }

    @RequestMapping(value = "/**")
    public String doService(HttpServletRequest request) throws Exception {
        return dispatcher.handleRequest(request);
    }

    protected abstract void initRules(UrlMappingRuleHelper rules);
}
