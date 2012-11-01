package com.astamuse.asta4d.web.dispatch;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.misc.spring.mvc.controller.AntPathRuleExtractor;
import com.astamuse.asta4d.template.TemplateResolver;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.interceptor.ForwardDescriptorHolder;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerInterceptor;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.ext.UrlMappingRuleHelper;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.dispatch.response.Asta4DPageProvider;
import com.astamuse.asta4d.web.dispatch.response.ContentProvider;
import com.astamuse.asta4d.web.dispatch.response.RedirectActionProvider;
import com.astamuse.asta4d.web.dispatch.response.forward.ForwardDescriptor;
import com.astamuse.asta4d.web.dispatch.response.forward.ForwardableException;

public class RequestDispatcherTest {

    private RequestDispatcher dispatcher = new RequestDispatcher();

    @BeforeTest
    public void setConf() {
        WebApplicationContext context = new WebApplicationContext();
        Context.setCurrentThreadContext(context);

        context.setConfiguration(new WebApplicationConfiguration());
        context.getConfiguration().setTemplateResolver(new TemplateResolver() {
            @Override
            public TemplateInfo loadResource(String path) {
                return createTemplateInfo(path, new ByteArrayInputStream(path.getBytes()));
            }
        });

        UrlMappingRuleHelper helper = new UrlMappingRuleHelper();
        initTestRules(helper);

        dispatcher.setRuleExtractor(new AntPathRuleExtractor());
        dispatcher.setRuleList(helper.getArrangedRuleList());

    }

    private void initTestRules(UrlMappingRuleHelper rules) {
        rules.add("/index", "/index.html");
    }

    @DataProvider(name = "data")
    public Object[][] getTestData() throws Exception {
        /*
        return new Object[][] { { "get", "/index", new Asta4DPageProvider("/index.html") },
                { new ReturnStringHandler(), new RequestHandlerInterceptor[0], "/test1.html" },
                { new ReturnDescriptorHandler(), new RequestHandlerInterceptor[0], "/test2.html" },
                { new ThrowDescriptorHandler(), new RequestHandlerInterceptor[0], "/test2.html" },
                { new VoidHandler(), new RequestHandlerInterceptor[] { new ViewChangeIntercepter() }, "/test4.html" },
                { new ThrowDescriptorHandler(), new RequestHandlerInterceptor[] { new CancelExceptionIntercepter() }, null } };
                */
        return new Object[][] { { "get", "/index", new Asta4DPageProvider("/index.html") } };
    }

    @Test(dataProvider = "data")
    public void execute(String method, String url, ContentProvider expectedContentProvider) throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(url);
        when(request.getContextPath()).thenReturn("");
        when(request.getMethod()).thenReturn(method);
        ContentProvider cp = dispatcher.handleRequest(request);
        assertContentProvider(cp, expectedContentProvider);

    }

    private void assertContentProvider(ContentProvider result, ContentProvider expected) {
        if (expected instanceof Asta4DPageProvider) {
            Assert.assertEquals(((Asta4DPageProvider) result).getPath(), ((Asta4DPageProvider) expected).getPath());
        } else if (expected instanceof RedirectActionProvider) {
            Assert.assertEquals(((RedirectActionProvider) result).getUrl(), ((RedirectActionProvider) expected).getUrl());
        } else {
            throw new UnsupportedOperationException(expected.getClass().toString());
        }
    }

    private UrlMappingRule getRule(Object... handlers) {
        UrlMappingRule rule = new UrlMappingRule();
        rule.setHandlerList(Arrays.asList(handlers));
        Map<Class<? extends ForwardDescriptor>, String> forwardDescriptors = new HashMap<>();
        forwardDescriptors.put(TestDescriptor.class, "/test2.html");
        rule.setForwardDescriptorMap(forwardDescriptors);
        return rule;
    }

    private static RequestHandlerInvoker getInvoker() {
        RequestHandlerInvokerFactory factory = new DefaultRequestHandlerInvokerFactory();
        return factory.getInvoker();
    }

    private abstract static class ExecutedCheckHandler {
        boolean executed = false;

        public void execute() {
            executed = true;
        }

        public boolean isExecuted() {
            return executed;
        }
    }

    private static class VoidHandler extends ExecutedCheckHandler {
        @RequestHandler
        public void handle() {
            super.execute();
        }
    }

    private static class ReturnStringHandler extends ExecutedCheckHandler {
        @RequestHandler
        public String handle() {
            super.execute();
            return "/test1.html";
        }
    }

    private static class ReturnDescriptorHandler extends ExecutedCheckHandler {
        @RequestHandler
        public ForwardDescriptor handle() {
            super.execute();
            return new TestDescriptor();
        }
    }

    private static class ThrowDescriptorHandler extends ExecutedCheckHandler {
        @RequestHandler
        public void handle() {
            super.execute();
            throw new ForwardableException(new TestDescriptor(), new IllegalArgumentException());
        }
    }

    private static class TestDescriptor implements ForwardDescriptor {

    }

    private static class ViewChangeDescriptor implements ForwardDescriptor {

    }

    private static class ViewChangeIntercepter implements RequestHandlerInterceptor {
        @Override
        public void preHandle(UrlMappingRule rule, ForwardDescriptorHolder holder) {
        }

        @Override
        public void postHandle(UrlMappingRule rule, ForwardDescriptorHolder holder, ExceptionHandler exceptionHandler) {
            holder.setForwardDescriptor(new ViewChangeDescriptor());
        }
    }

    private static class CancelExceptionIntercepter implements RequestHandlerInterceptor {
        @Override
        public void preHandle(UrlMappingRule rule, ForwardDescriptorHolder holder) {
        }

        @Override
        public void postHandle(UrlMappingRule rule, ForwardDescriptorHolder holder, ExceptionHandler exceptionHandler) {
            exceptionHandler.setException(null);
        }

    }
}
