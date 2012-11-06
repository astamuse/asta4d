package com.astamuse.asta4d.web.dispatch;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.misc.spring.mvc.controller.AntPathRuleExtractor;
import com.astamuse.asta4d.template.TemplateResolver;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.annotation.ContentProvider;
import com.astamuse.asta4d.web.dispatch.mapping.ext.UrlMappingRuleHelper;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.dispatch.response.Asta4DPageWriter;
import com.astamuse.asta4d.web.dispatch.response.ContentWriter;
import com.astamuse.asta4d.web.dispatch.response.JsonWriter;
import com.astamuse.asta4d.web.dispatch.response.RedirectActionWriter;
import com.astamuse.asta4d.web.dispatch.response.provider.Asta4DPageProvider;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectTargetProvider;
import com.astamuse.asta4d.web.dispatch.response.provider.RestResult;
import com.astamuse.asta4d.web.dispatch.response.provider.RestResultProvider;
import com.astamuse.asta4d.web.util.AnnotationMethodHelper;

public class RequestDispatcherTest {

    private RequestDispatcher dispatcher = new RequestDispatcher();

    private WebApplicationConfiguration configuration = new WebApplicationConfiguration() {
        {
            setTemplateResolver(new TemplateResolver() {
                @Override
                public TemplateInfo loadResource(String path) {
                    return createTemplateInfo(path, new ByteArrayInputStream(path.getBytes()));
                }
            });
        }
    };

    @BeforeTest
    public void setConf() {
        WebApplicationContext context = new WebApplicationContext();
        Context.setCurrentThreadContext(context);
        context.setConfiguration(configuration);

        UrlMappingRuleHelper helper = new UrlMappingRuleHelper();
        initTestRules(helper);

        dispatcher.setRuleExtractor(new AntPathRuleExtractor());
        dispatcher.setRuleList(helper.getArrangedRuleList());

    }

    @BeforeMethod
    public void initContext() {
        Context context = Context.getCurrentThreadContext();
        if (context == null) {
            context = new WebApplicationContext();
            Context.setCurrentThreadContext(context);
        }
        context.clearSavedData();
        WebApplicationContext webContext = (WebApplicationContext) context;
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        webContext.setRequest(request);
        webContext.setResponse(response);
    }

    private void initTestRules(UrlMappingRuleHelper rules) {
        rules.add("/index", "/index.html");
        rules.add("/go-redirect", "/go-redirect/ok");
        rules.add(HttpMethod.DELETE, "/restapi", RestResultProvider.class).handler(TestRestApiHandler.class);
        rules.add("/getjson", TestJsonQuery.class);
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
        return new Object[][] { { "get", "/index", 0, new Asta4DPageProvider("/index.html"), new Asta4DPageWriter() },
                { "get", "/go-redirect", 0, new RedirectTargetProvider("/go-redirect/ok"), new RedirectActionWriter() },
                { "delete", "/restapi", 401, null, null }, { "get", "/getjson", 0, new TestJsonQuery(), new JsonWriter() } };
    }

    @Test(dataProvider = "data")
    public void execute(String method, String url, int status, Object expectedResult, ContentWriter cw) throws Exception {
        WebApplicationContext context = (WebApplicationContext) Context.getCurrentThreadContext();
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        HttpSession session = mock(HttpSession.class);

        when(request.getParameterNames()).thenReturn(Collections.emptyEnumeration());
        when(request.getCookies()).thenReturn(new Cookie[0]);
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        when(request.getSession(true)).thenReturn(session);

        when(request.getRequestURI()).thenReturn(url);
        when(request.getContextPath()).thenReturn("");
        when(request.getMethod()).thenReturn(method);

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                bos.write(b);
            }
        });
        dispatcher.dispatchAndProcess(request, response);

        if (status != 0) {
            verify(response).setStatus(status);
        }

        if (expectedResult == null) {
            return;
        }

        final ByteArrayOutputStream expectedBos = new ByteArrayOutputStream();
        HttpServletResponse expectedResponse = mock(HttpServletResponse.class);
        when(expectedResponse.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                expectedBos.write(b);
            }
        });
        if (expectedResult instanceof RedirectTargetProvider) {
            // how test?
        } else {

            Method m = AnnotationMethodHelper.findMethod(expectedResult, ContentProvider.class);
            Object[] params = InjectUtil.getMethodInjectParams(m);
            if (params == null) {
                params = new Object[0];
            }
            Object content;
            content = m.invoke(expectedResult, params);

            cw.writeResponse(expectedResponse, content);

            Assert.assertEquals(bos.toByteArray(), expectedBos.toByteArray());

        }
    }

    public static class TestRestApiHandler {

        @RequestHandler
        public RestResult doDelete() {
            return new RestResult(401);
        }
    }

    public static class TestJsonObject {
        private int value = 0;

        public TestJsonObject() {

        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class TestJsonQuery {

        @ContentProvider(writer = JsonWriter.class)
        public TestJsonObject query() {
            TestJsonObject obj = new TestJsonObject();
            obj.setValue(123);
            return obj;
        }
    }

    /*
        private void assertContentProvider(ContentProvider result, ContentProvider expected) {
            if (expected instanceof Asta4DPageProvider) {
                Assert.assertEquals(((Asta4DPageProvider) result).getPath(), ((Asta4DPageProvider) expected).getPath());
            } else if (expected instanceof RedirectTargetProvider) {
                Assert.assertEquals(((RedirectTargetProvider) result).getUrl(), ((RedirectTargetProvider) expected).getUrl());
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
            public void preHandle(UrlMappingRule rule, RequestHandlerResultHolder holder) {
            }

            @Override
            public void postHandle(UrlMappingRule rule, RequestHandlerResultHolder holder, ExceptionHandler exceptionHandler) {
                holder.setForwardDescriptor(new ViewChangeDescriptor());
            }
        }

        private static class CancelExceptionIntercepter implements RequestHandlerInterceptor {
            @Override
            public void preHandle(UrlMappingRule rule, RequestHandlerResultHolder holder) {
            }

            @Override
            public void postHandle(UrlMappingRule rule, RequestHandlerResultHolder holder, ExceptionHandler exceptionHandler) {
                exceptionHandler.setException(null);
            }

        }
        */
}
