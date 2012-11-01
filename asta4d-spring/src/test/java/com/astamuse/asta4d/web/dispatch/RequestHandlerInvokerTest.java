package com.astamuse.asta4d.web.dispatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.interceptor.ForwardDescriptorHolder;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerInterceptor;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.dispatch.response.forward.ForwardDescriptor;
import com.astamuse.asta4d.web.dispatch.response.forward.ForwardableException;

public class RequestHandlerInvokerTest {

    @BeforeTest
    public void setConf() {
        WebApplicationContext context = new WebApplicationContext();
        context.setConfiguration(new WebApplicationConfiguration());
        Context.setCurrentThreadContext(context);
    }

    @DataProvider(name = "data")
    public Object[][] getTestData() {
        return new Object[][] { { new VoidHandler(), new RequestHandlerInterceptor[0], null },
                { new ReturnStringHandler(), new RequestHandlerInterceptor[0], "/test1.html" },
                { new ReturnDescriptorHandler(), new RequestHandlerInterceptor[0], "/test2.html" },
                { new ThrowDescriptorHandler(), new RequestHandlerInterceptor[0], "/test2.html" },
                { new VoidHandler(), new RequestHandlerInterceptor[] { new ViewChangeIntercepter() }, "/test4.html" },
                { new ThrowDescriptorHandler(), new RequestHandlerInterceptor[] { new CancelExceptionIntercepter() }, null } };
    }

    @Test(dataProvider = "data")
    public void executeInvoker(ExecutedCheckHandler handler, RequestHandlerInterceptor[] interceptors, String expectedPath)
            throws Exception {
        // TODO we need a http request mockup to test from dispatcher rather
        // than test from invoker
        /*
        RequestHandlerInvoker invoker = getInvoker();
        Asta4dView view = invoker.invoke(getRule(handler));
        if (expectedPath == null) {
            Assert.assertNull(view);
        } else {
            assertEquals(((WebPageView) view).getPath(), expectedPath);
        }
        assertTrue(handler.isExecuted());
        */
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
