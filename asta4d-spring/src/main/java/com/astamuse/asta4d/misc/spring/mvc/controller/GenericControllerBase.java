package com.astamuse.asta4d.misc.spring.mvc.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.astamuse.asta4d.misc.spring.mvc.SpringWebPageView;
import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.web.dispatch.RequestDispatcher;
import com.astamuse.asta4d.web.dispatch.RequestHandlerAdapter;
import com.astamuse.asta4d.web.dispatch.mapping.ext.RequestHandlerBuilder;
import com.astamuse.asta4d.web.dispatch.mapping.ext.UrlMappingRuleHelper;
import com.astamuse.asta4d.web.view.Asta4dView;
import com.astamuse.asta4d.web.view.RedirectView;
import com.astamuse.asta4d.web.view.WebPageView;

//TODO need to cache the mapped result
@Controller
public abstract class GenericControllerBase implements ApplicationContextAware {

    private final static class SpringManagedRequestHandlerBuilder implements RequestHandlerBuilder {

        private GenericControllerBase genericController;

        SpringManagedRequestHandlerBuilder(GenericControllerBase genericController) {
            this.genericController = genericController;
        }

        @Override
        public Object createRequestHandler(Object declaration) {
            return new SpringManagedRequestHandlerAdapter(genericController, declaration);
        }
    }

    private final static class SpringManagedRequestHandlerAdapter implements RequestHandlerAdapter {

        private GenericControllerBase genericController;

        private Class<?> beanCls = null;

        private String beanId = null;

        SpringManagedRequestHandlerAdapter(GenericControllerBase genericController, Object declaration) {
            this.genericController = genericController;
            if (declaration instanceof Class) {
                beanCls = (Class<?>) declaration;
            } else if (declaration instanceof String) {
                beanId = declaration.toString();
            }
        }

        @Override
        public Object asRequestHandler() {
            if (beanCls != null) {
                return genericController.beanCtx.getBean(beanCls);
            } else if (beanId != null) {
                return genericController.beanCtx.getBean(beanId);
            } else {
                return null;
            }
        }
    }

    private ApplicationContext beanCtx = null;

    private RequestDispatcher dispatcher = new RequestDispatcher();

    public GenericControllerBase() {
        UrlMappingRuleHelper helper = new UrlMappingRuleHelper();
        helper.addRequestHandlerBuilder(new SpringManagedRequestHandlerBuilder(this));
        initUrlMappingRules(helper);
        dispatcher.setRuleExtractor(new AntPathRuleExtractor());
        dispatcher.setRuleList(helper.getSortedRuleList());
    }

    @RequestMapping(value = "/**")
    public View doService(HttpServletRequest request) throws Exception {
        Asta4dView view = dispatcher.handleRequest(request);
        return convertSpringView(view);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.beanCtx = context;
    }

    protected abstract void initUrlMappingRules(UrlMappingRuleHelper rules);

    private View convertSpringView(Asta4dView view) throws TemplateException {
        if (view instanceof WebPageView) {
            WebPageView pageView = (WebPageView) view;
            return new SpringWebPageView(pageView.getPath());
        }
        if (view instanceof RedirectView) {
            RedirectView redirectView = (RedirectView) view;
            return new org.springframework.web.servlet.view.RedirectView(redirectView.getUrl(), redirectView.isContextRelative(),
                    redirectView.isHttp10Compatible(), redirectView.isExposeModelAttributes());
        }
        throw new UnsupportedOperationException("View Type:" + view.getClass().getName());
    }
}
