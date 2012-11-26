package com.astamuse.asta4d.misc.spring.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.AntPathRuleExtractor;
import com.astamuse.asta4d.web.dispatch.RequestDispatcher;
import com.astamuse.asta4d.web.dispatch.mapping.ext.UrlMappingRuleHelper;

//TODO need to cache the mapped result
@Controller
public abstract class GenericControllerBase implements ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(GenericControllerBase.class);

    private ApplicationContext beanCtx = null;

    private RequestDispatcher dispatcher = new RequestDispatcher();

    public void init() {
        Context templateContext = Context.getCurrentThreadContext();
        if (templateContext == null) {
            templateContext = beanCtx.getBean(WebApplicationContext.class);
            Context.setCurrentThreadContext(templateContext);
        }
        UrlMappingRuleHelper helper = new UrlMappingRuleHelper();
        initUrlMappingRules(helper);
        dispatcher.setRuleExtractor(new AntPathRuleExtractor());
        dispatcher.setRuleList(helper.getArrangedRuleList());
        logger.info("url mapping rules are initialized.");
    }

    @RequestMapping(value = "/**")
    public void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        dispatcher.dispatchAndProcess(request, response);
        /*
        Object contentProvider = dispatcher.handleRequest(request);
        return contentProvider == null ? null : convertSpringView(contentProvider);
        */
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.beanCtx = context;
        // we have to inovke init here because the
        // SpringManagedRequestHandlerResolver need to call application context.
        // And there is no matter that dispatcher is initialized in multi times,
        // so we do not apply a lock here.
        init();
    }

    protected abstract void initUrlMappingRules(UrlMappingRuleHelper rules);
    /*
        private View convertSpringView(ContentProvider contentProvider) throws TemplateException {
            if (contentProvider instanceof Asta4DPageProvider) {
                return new SpringWebPageView((Asta4DPageProvider) contentProvider);
            } else if (contentProvider instanceof RedirectActionProvider) {
                RedirectActionProvider redirector = (RedirectActionProvider) contentProvider;
                String url = RedirectUtil.setFlashScopeData(redirector.getUrl(), redirector.getFlashScopeData());
                return new org.springframework.web.servlet.view.RedirectView(url);
            }
            throw new UnsupportedOperationException("ContentProvider Type:" + contentProvider.getClass().getName());
        }
    */
}
