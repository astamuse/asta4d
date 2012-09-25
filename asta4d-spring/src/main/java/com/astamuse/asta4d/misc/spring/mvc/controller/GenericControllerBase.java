package com.astamuse.asta4d.misc.spring.mvc.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.View;

import com.astamuse.asta4d.misc.spring.mvc.SpringWebPageView;
import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.web.dispatch.RequestDispatcher;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleHelper;
import com.astamuse.asta4d.web.view.Asta4dView;
import com.astamuse.asta4d.web.view.RedirectView;
import com.astamuse.asta4d.web.view.WebPageView;

//TODO need to cache the mapped result
@Controller
public abstract class GenericControllerBase implements ApplicationContextAware {

    private ApplicationContext beanCtx;

    private RequestDispatcher dispatcher = new RequestDispatcher();

    private void init() {
        UrlMappingRuleHelper rules = new UrlMappingRuleHelper();
        initUrlMappingRules(beanCtx, rules);
        dispatcher.setRuleExtractor(new AntPathRuleExtractor());
        dispatcher.setRuleList(rules.getSortedRuleList());
    }

    @RequestMapping(value = "/**")
    public View doService(HttpServletRequest request) throws Exception {
        Asta4dView view = dispatcher.handleRequest(request, getLocale(request));
        return convertSpringView(view);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.beanCtx = context;
        // we have to inovke init here because the child class would want to
        // call application context. And there is no matter that dispatcher is
        // initialized in multi times, so we do not apply a lock here.
        init();
    }

    protected abstract void initUrlMappingRules(ApplicationContext beanCtx, UrlMappingRuleHelper rules);

    private Locale getLocale(HttpServletRequest request) {
        LocaleResolver localeResolver = beanCtx.getBean("localeResolver", LocaleResolver.class);
        return localeResolver.resolveLocale(request);
    }

    private View convertSpringView(Asta4dView view) throws TemplateException {
        if (view instanceof WebPageView) {
            WebPageView pageView = (WebPageView) view;
            return new SpringWebPageView(pageView.getPath(), pageView.getLocale());
        }
        if (view instanceof RedirectView) {
            RedirectView redirectView = (RedirectView) view;
            return new org.springframework.web.servlet.view.RedirectView(redirectView.getUrl(), redirectView.isContextRelative(),
                    redirectView.isHttp10Compatible(), redirectView.isExposeModelAttributes());
        }
        throw new UnsupportedOperationException("View Type:" + view.getClass().getName());
    }
}
