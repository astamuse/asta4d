package com.astamuse.asta4d.misc.spring.mvc.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.astamuse.asta4d.misc.spring.mvc.SpringWebPageView;
import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.web.dispatch.RequestDispatcher;
import com.astamuse.asta4d.web.dispatch.mapping.ext.RequestHandlerResolver;
import com.astamuse.asta4d.web.dispatch.mapping.ext.UrlMappingRuleHelper;
import com.astamuse.asta4d.web.dispatch.request.RequestHandlerAdapter;
import com.astamuse.asta4d.web.dispatch.response.Asta4DPageProvider;
import com.astamuse.asta4d.web.dispatch.response.ContentProvider;
import com.astamuse.asta4d.web.dispatch.response.RedirectActionProvider;
import com.astamuse.asta4d.web.util.RedirectUtil;

//TODO need to cache the mapped result
@Controller
public abstract class GenericControllerBase implements ApplicationContextAware {

    private final static class SpringManagedRequestHandlerResolver implements RequestHandlerResolver {

        private ApplicationContext beanCtx;

        SpringManagedRequestHandlerResolver(ApplicationContext beanCtx) {
            this.beanCtx = beanCtx;
        }

        @Override
        public Object resolve(Object declaration) {

            if (declaration instanceof Class) {
                Class<?> beanCls = (Class<?>) declaration;
                String[] names = beanCtx.getBeanNamesForType(beanCls);
                boolean beanExist = false;
                for (String name : names) {
                    if (beanCtx.containsBean(name)) {
                        beanExist = true;
                        break;
                    }
                }
                if (beanExist) {
                    return new SpringManagedRequestHandlerAdapter(beanCtx, beanCls, null);
                } else {
                    return null;
                }
            } else if (declaration instanceof String) {
                String beanId = declaration.toString();
                if (beanCtx.containsBean(beanId)) {
                    return new SpringManagedRequestHandlerAdapter(beanCtx, null, beanId);
                } else {
                    return null;
                }
            }
            return null;

        }
    }

    private final static class SpringManagedRequestHandlerAdapter implements RequestHandlerAdapter {

        private ApplicationContext beanCtx;

        private Class<?> beanCls = null;

        private String beanId = null;

        SpringManagedRequestHandlerAdapter(ApplicationContext beanCtx, Class<?> beanCls, String beanId) {
            this.beanCtx = beanCtx;
            this.beanCls = beanCls;
            this.beanId = beanId;
        }

        @Override
        public Object asRequestHandler() {
            if (beanCls != null) {
                return beanCtx.getBean(beanCls);
            } else if (beanId != null) {
                return beanCtx.getBean(beanId);
            } else {
                return null;
            }
        }
    }

    private final static Logger logger = LoggerFactory.getLogger(GenericControllerBase.class);

    private ApplicationContext beanCtx = null;

    private RequestDispatcher dispatcher = new RequestDispatcher();

    public void init() {
        UrlMappingRuleHelper helper = new UrlMappingRuleHelper();
        helper.addRequestHandlerResolver(new SpringManagedRequestHandlerResolver(beanCtx));
        initUrlMappingRules(helper);
        dispatcher.setRuleExtractor(new AntPathRuleExtractor());
        dispatcher.setRuleList(helper.getArrangedRuleList());
        logger.info("url mapping rules are initialized.");
    }

    @RequestMapping(value = "/**")
    public View doService(HttpServletRequest request) throws Exception {
        ContentProvider contentProvider = dispatcher.handleRequest(request);
        return contentProvider == null ? null : convertSpringView(contentProvider);
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
}
