/*
 * Copyright 2012 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.astamuse.asta4d.web.dispatch;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.Page;
import com.astamuse.asta4d.template.TemplateResolver;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.ext.UrlMappingRuleHelper;
import com.astamuse.asta4d.web.dispatch.mapping.ext.UrlMappingRuleRewriter;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.dispatch.response.provider.HeaderInfo;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectDescriptor;
import com.astamuse.asta4d.web.dispatch.response.writer.Asta4DPageWriter;
import com.astamuse.asta4d.web.dispatch.response.writer.ContentWriter;
import com.astamuse.asta4d.web.dispatch.response.writer.JsonWriter;
import com.astamuse.asta4d.web.dispatch.response.writer.RedirectActionWriter;

public class RequestDispatcherTest {

    private RequestDispatcher dispatcher = new RequestDispatcher();

    private static WebApplicationConfiguration configuration = new WebApplicationConfiguration() {
        {
            setTemplateResolver(new TemplateResolver() {
                @Override
                public TemplateInfo loadResource(String path) {
                    return createTemplateInfo(path, new ByteArrayInputStream(path.getBytes()));
                }
            });
        }
    };
    static {
        Configuration.setConfiguration(configuration);
    }

    @BeforeTest
    public void setConf() {
        WebApplicationContext context = new WebApplicationContext();
        Context.setCurrentThreadContext(context);
    }

    @BeforeMethod
    public void initContext() {
        Context context = Context.getCurrentThreadContext();
        if (context == null) {
            context = new WebApplicationContext();
            Context.setCurrentThreadContext(context);
        }
        context.clear();
        WebApplicationContext webContext = (WebApplicationContext) context;
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        webContext.setRequest(request);
        webContext.setResponse(response);
    }

    private void initTestRules(UrlMappingRuleHelper rules) {

        rules.addRuleRewriter(new UrlMappingRuleRewriter() {
            @Override
            public void rewrite(UrlMappingRule rule) {
                if (rule.getSourcePath().equals("/rewrite-attr")) {
                    rule.getAttributeList().add("rewrite-attr");
                }
            }
        });

        rules.addDefaultRequestHandler("rewrite-attr", new TestJsonHandler(358));

        rules.addGlobalForward(NullPointerException.class, "/NullPointerException", 501);
        rules.addGlobalForward(Exception.class, "/Exception", 500);

        //@formatter:off
        
        rules.add("/index").id("index-page")
                           .forward(Throwable.class, "/error.html", 500)
                           .forward("/index.html");
        
        rules.add("/index-duplicated").reMapTo("index-page");

        rules.add("/body-only", "/bodyOnly.html").attribute(Asta4DPageWriter.AttrBodyOnly);
        
        rules.add("/go-redirect").redirect("/go-redirect/ok");
        
        rules.add(HttpMethod.DELETE, "/restapi").handler(TestRestApiHandler.class).rest();
        
        rules.add("/getjson").handler(new TestJsonHandler(123)).json();
        rules.add("/rewrite-attr").json();
        
        rules.add("/thrownep").handler(ThrowNEPHandler.class).forward("/thrownep");
        rules.add("/throwexception").handler(ThrowExceptionHandler.class).forward("/throwexception");
        
        rules.add("/**/*").forward("/notfound", 404);
      //@formatter:on
    }

    @DataProvider(name = "data")
    public Object[][] getTestData() throws Exception {
        //@formatter:off
        return new Object[][] { 
                { "get", "/index", 0, new Page("/index.html"), new Asta4DPageWriter() },
                { "get", "/index-rewrite", 0, new Page("/index.html"), new Asta4DPageWriter() },
                { "get", "/index-duplicated", 0, new Page("/index.html"), new Asta4DPageWriter() },
                { "get", "/body-only", 0, new Page("/bodyOnly.html"), new Asta4DPageWriter() },
                { "get", "/go-redirect", 0, new RedirectDescriptor("/go-redirect/ok", null), new RedirectActionWriter() },
                { "delete", "/restapi", 401, null, null }, 
                { "get", "/getjson", 0, new TestJsonObject(123), new JsonWriter() },
                { "get", "/rewrite-attr", 0, new TestJsonObject(358), new JsonWriter() },
                { "get", "/nofile", 404, new Page("/notfound"), new Asta4DPageWriter() },
                { "get", "/thrownep", 501, new Page("/NullPointerException"), new Asta4DPageWriter() },
                { "get", "/throwexception", 500, new Page("/Exception"), new Asta4DPageWriter() },
                };
        //@formatter:on
    }

    @Test(dataProvider = "data")
    public void execute(String method, String url, int status, Object expectedContent, ContentWriter cw) throws Exception {
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

        UrlMappingRuleHelper helper = new UrlMappingRuleHelper();
        initTestRules(helper);

        if (url.equals("/index-rewrite")) {
            context.setAccessURI("/index");
        }

        dispatcher.dispatchAndProcess(helper.getArrangedRuleList());

        if (status != 0) {
            verify(response).setStatus(status);
        }

        if (expectedContent == null) {
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
        if (expectedContent instanceof RedirectDescriptor) {
            // how test?
        } else {
            UrlMappingRule currentRule = context.getData(RequestDispatcher.KEY_CURRENT_RULE);
            cw.writeResponse(currentRule, expectedResponse, expectedContent);

            Assert.assertEquals(new String(bos.toByteArray()), new String(expectedBos.toByteArray()));

        }
    }

    public static class TestRestApiHandler {

        @RequestHandler
        public HeaderInfo doDelete() {
            return new HeaderInfo(401);
        }
    }

    public static class TestJsonObject {
        private int value = 0;

        public TestJsonObject(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class TestJsonHandler {

        private int value;

        public TestJsonHandler(int value) {
            this.value = value;
        }

        @RequestHandler
        public TestJsonObject handle() {
            TestJsonObject obj = new TestJsonObject(value);
            return obj;
        }
    }

    public static class ThrowNEPHandler {
        @RequestHandler
        public Object foo() {
            throw new NullPointerException();
        }
    }

    public static class ThrowExceptionHandler {
        @RequestHandler
        public Object foo() {
            throw new RuntimeException();
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
