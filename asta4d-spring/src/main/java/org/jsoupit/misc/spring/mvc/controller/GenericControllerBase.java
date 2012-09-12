package org.jsoupit.misc.spring.mvc.controller;

import javax.servlet.http.HttpServletRequest;

import org.jsoupit.web.dispatch.RequestDispatcher;
import org.jsoupit.web.dispatch.mapping.UrlMappingRuleHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
