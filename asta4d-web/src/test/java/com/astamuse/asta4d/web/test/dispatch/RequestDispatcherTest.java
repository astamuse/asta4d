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

package com.astamuse.asta4d.web.test.dispatch;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.tuple.Pair;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.Page;
import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.template.AbstractTemplateResolver;
import com.astamuse.asta4d.template.TemplateNotFoundException;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.AntPathRuleMatcher;
import com.astamuse.asta4d.web.dispatch.DispatcherRuleMatcher;
import com.astamuse.asta4d.web.dispatch.HttpMethod;
import com.astamuse.asta4d.web.dispatch.HttpMethod.ExtendHttpMethod;
import com.astamuse.asta4d.web.dispatch.RequestDispatcher;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerInterceptor;
import com.astamuse.asta4d.web.dispatch.interceptor.RequestHandlerResultHolder;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingResult;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRuleRewriter;
import com.astamuse.asta4d.web.dispatch.mapping.handy.HandyRuleSet;
import com.astamuse.asta4d.web.dispatch.request.RequestHandler;
import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;
import com.astamuse.asta4d.web.dispatch.response.provider.Asta4DPageProvider;
import com.astamuse.asta4d.web.dispatch.response.provider.ContentProvider;
import com.astamuse.asta4d.web.dispatch.response.provider.HeaderInfoProvider;
import com.astamuse.asta4d.web.util.bean.DeclareInstanceAdapter;

public class RequestDispatcherTest {

    private static class JsonContentNotFoundException extends RuntimeException {

    }

    private RequestDispatcher dispatcher = new RequestDispatcher();

    private static WebApplicationConfiguration configuration = new WebApplicationConfiguration() {
        {
            setTemplateResolver(new AbstractTemplateResolver() {
                @Override
                public TemplateInfo loadResource(String path) {
                    if (path.equals("/template-not-exists")) {
                        return null;
                    } else {
                        return createTemplateInfo(path, new ByteArrayInputStream(path.getBytes()));
                    }
                }
            });
        }
    };

    @BeforeClass
    public void setConf() {
        Locale.setDefault(Locale.ROOT);
        Configuration.setConfiguration(configuration);
    }

    @BeforeMethod
    public void initContext() {
        Context context = Context.getCurrentThreadContext();
        if (context == null) {
            context = new WebApplicationContext();
            Context.setCurrentThreadContext(context);
        }
        context.init();
        WebApplicationContext webContext = (WebApplicationContext) context;
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        webContext.setRequest(request);
        webContext.setResponse(response);
    }

    private void initTestRules(HandyRuleSet rules) {

        rules.addRuleRewriter(new UrlMappingRuleRewriter() {
            @Override
            public void rewrite(UrlMappingRule rule) {
                if (rule.getSourcePath().equals("/rewrite-attr")) {
                    rule.getAttributeList().add("rewrite-attr");
                }
            }
        });

        // this is for a bug that missing DelcaredInstanceUtil.createInstance
        // calling when add default handler
        rules.addDefaultRequestHandler("rewrite-attr", DoNothingHandler.class);

        rules.addDefaultRequestHandler("rewrite-attr", new TestJsonHandler(new TestJsonObject(358)));

        rules.addDefaultRequestHandler("remap-with-attr", new Object() {

            @RequestHandler
            public String handle(String indexVar, String extraPath, UrlMappingRule rule) {
                if (!rule.hasAttribute("index-original")) {
                    throw new RuntimeException("index-remap-original not found");
                }
                return "/index-" + indexVar + "-" + extraPath + ".html";
            }

        });

        rules.addRequestHandlerInterceptor(new CounterInterceptorAdapter());
        rules.addRequestHandlerInterceptor(new CounterInterceptorAdapter());

        rules.addGlobalForward(TemplateNotFoundException.class, "/notfound", 404);
        rules.addGlobalForward(NullPointerException.class, "/NullPointerException", 501);
        rules.addGlobalForward(Exception.class, "/Exception", 500);

        rules.registerJsonTransformer(new ResultTransformer() {
            @Override
            public Object transformToContentProvider(Object result) {
                if (result instanceof JsonContentNotFoundException) {
                    return new HeaderInfoProvider(404);
                } else {
                    return null;
                }
            }
        });

        //@formatter:off
        
        rules.add("/index").id("index-page").attribute("index-original").var("indexVar", "index_var")
                           .forward(Throwable.class, "/error.html", 500)
                           .forward("/index.html");
        

        
        rules.add("/index-duplicated").reMapTo("index-page");
        
        
        rules.add("/index-remap-with-attr").reMapTo("index-page").attribute("remap-with-attr").var("extraPath", "extra_path");

        rules.add("/body-only", "/bodyOnly.html").attribute(Asta4DPageProvider.AttrBodyOnly);
        
        rules.add("/go-redirect").redirect("/go-redirect/ok");
        rules.add("/go-redirect-301").redirect("301:/go-redirect/301");
        rules.add("/go-redirect-302").redirect("302:/go-redirect/302");
        rules.add("/go-redirect-p").redirect("p:/go-redirect/p");
        rules.add("/go-redirect-t").redirect("t:/go-redirect/t");
        
        rules.add(HttpMethod.DELETE, "/restapi").handler(TestRestApiHandler.class).xml();
        
        rules.add("/getjson").handler(new TestJsonHandler(new TestJsonObject(123))).json();
        rules.add("/rewrite-attr").json();

        rules.add("/json404").handler(new TestJsonHandler(new JsonContentNotFoundException())).json();
        
        rules.add("/json500").handler(new TestJsonHandler(new RuntimeException())).json();
        
        // variableinjection (default and regex pattern)
        rules.add("/variableinjection/{var_1}/{var_2}", "/templates/variableinjection.html");
        rules.add("/variableinjection_regex/{var_1}/{var_2:[0-9]+}", "/templates/variableinjection_regex.html");
        
        // custom matcher
        rules.add("/custom_matcher/{var}", "/templates/custom_matcher.html").matcher(new DispatcherRuleMatcher() {
            @Override
            public UrlMappingResult match(UrlMappingRule rule, HttpMethod method, ExtendHttpMethod extendMethod, String uri, String queryString) {
                UrlMappingResult result = null;
                if(!uri.endsWith("NotFound")){
                    result = new AntPathRuleMatcher().match(rule, method, extendMethod, uri, queryString);
                }
                return result;
            }
        });

        rules.add("/template-not-exists","/template-not-exists");
        rules.add("/thrownep").handler(ThrowNEPHandler.class).forward("/thrownep");
        rules.add("/throwexception").handler(ThrowExceptionHandler.class).forward("/throwexception");
        
        rules.add(ExtendHttpMethod.of("PROPPATCH"), "/index", "/index-proppatch.html");
        
        rules.add("/**/*").forward("/notfound", 404);
      //@formatter:on
    }

    private final static Asta4DPageProvider getExpectedPage(final String path) throws Exception {
        Context ctx = new WebApplicationContext();
        ctx.init();
        return Context.with(ctx, new Callable<Asta4DPageProvider>() {
            @Override
            public Asta4DPageProvider call() throws Exception {
                return new Asta4DPageProvider(Page.buildFromPath(path));
            }
        });

    }

    @DataProvider(name = "data")
    public Object[][] getTestData() throws Exception {
        //@formatter:off
        return new Object[][] { 
            /*
                { "get", "/index", 0, getExpectedPage("/index.html")},
                
                { "get", "/index-rewrite", 0, getExpectedPage("/index.html") },
                { "get", "/index-duplicated", 0, getExpectedPage("/index.html") },
                { "get", "/index-remap-with-attr", 0, getExpectedPage("/index-index_var-extra_path.html") },
                { "get", "/body-only", 0, getExpectedPage("/bodyOnly.html") },
                { "get", "/go-redirect", 302, new RedirectTargetProvider(302, "/go-redirect/ok", null)},
                { "get", "/go-redirect-301", 301, new RedirectTargetProvider(301, "/go-redirect/301", null)},
                { "get", "/go-redirect-302", 302, new RedirectTargetProvider(302, "/go-redirect/302", null)},
                { "get", "/go-redirect-p", 301, new RedirectTargetProvider(301, "/go-redirect/p", null)},
                { "get", "/go-redirect-t", 302, new RedirectTargetProvider(302, "/go-redirect/t", null)},
                
                { "delete", "/restapi", 401, null }, 
                { "get", "/getjson", 0, new JsonDataProvider(new TestJsonObject(123))},
                { "get", "/rewrite-attr", 0, new JsonDataProvider(new TestJsonObject(358)) },
                { "get", "/json404", 404, new HeaderInfoProvider(404) },
                { "get", "/json500", 500, new HeaderInfoProvider(500) },
                
                //TODO it seems that there is missing the way to declare return status for json transforming when exceptions occur 
                //{ "get", "/jsonerror", 500, new JsonDataProvider(TestExceptionInstance) },

                // variableinjection (default and regex pattern)
                { "get", "/variableinjection/foo/25", 0, getExpectedPage("/templates/variableinjection.html")},
                { "get", "/variableinjection/foo/NaN", 0, getExpectedPage("/templates/variableinjection.html")},
                { "get", "/variableinjection_regex/foo/25", 0, getExpectedPage("/templates/variableinjection_regex.html")},
                { "get", "/variableinjection_regex/foo/NaN", 404, getExpectedPage("/notfound")},
                
                // custom matcher
                { "get", "/custom_matcher/NotFound", 404, getExpectedPage("/notfound")},
                { "get", "/custom_matcher/Found", 0, getExpectedPage("/templates/custom_matcher.html")},

                { "get", "/nofile", 404, getExpectedPage("/notfound")},
                
                
                { "get", "/template-not-exists", 404, getExpectedPage("/notfound")},

                { "get", "/thrownep", 501, getExpectedPage("/NullPointerException")},
                { "get", "/throwexception", 500, getExpectedPage("/Exception")},
                
                // to handle the extending http methods out of predeinfed methods by the framework
                { "propfind", "/index", 404, new HeaderInfoProvider(404)},
                { "proppatch", "/index", 0, getExpectedPage("/index-proppatch.html")},
                */
                
                //{ "get", "/template-not-exists", 404, getExpectedPage("/notfound")},
                { "get", "/thrownep", 501, getExpectedPage("/NullPointerException")},

                };
        //@formatter:on
    }

    @Test(dataProvider = "data")
    public void execute(String method, String url, int status, ContentProvider contentProvider) throws Exception {
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

        final ByteArrayOutputStream responseBos = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                responseBos.write(b);
            }
        });

        HandyRuleSet ruleSet = new HandyRuleSet();
        initTestRules(ruleSet);

        if (url.equals("/index-rewrite")) {
            context.setAccessURI("/index");
        }

        dispatcher.dispatchAndProcess(ruleSet.getArrangedRuleList());

        // verify status at first then when contentProvider is null, we do not
        // need to do more verification
        if (status != 0) {
            verify(response).setStatus(status);
        }

        if (contentProvider == null) {
            return;
        }

        // prepare expected results
        HttpServletResponse expectedResponse = mock(HttpServletResponse.class);

        final ByteArrayOutputStream expectedBos = new ByteArrayOutputStream();
        when(expectedResponse.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                expectedBos.write(b);
            }
        });

        final List<Pair<String, String>> expectedHeaderList = new LinkedList<>();

        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                expectedHeaderList.add(Pair.of((String) args[0], (String) args[1]));
                return null;
            }
        }).when(expectedResponse).addHeader(anyString(), anyString());

        UrlMappingRule currentRule = context.getCurrentRule();
        contentProvider.produce(currentRule, expectedResponse);

        // verify extra contents like headers and output stream

        for (Pair<String, String> pair : expectedHeaderList) {
            verify(response).addHeader(pair.getKey(), pair.getValue());
        }

        Assert.assertEquals(new String(responseBos.toByteArray()), new String(expectedBos.toByteArray()));

    }

    public static class TestRestApiHandler {

        @RequestHandler
        public HeaderInfoProvider doDelete() {
            return new HeaderInfoProvider(401);
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

        private Object returnValue;

        public TestJsonHandler(Object returnValue) {
            this.returnValue = returnValue;
        }

        @RequestHandler
        public Object handle() {
            return returnValue;
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

    public static class DoNothingHandler {
        @RequestHandler
        public void foo() {
            //
        }
    }

    public static class CounterInterceptor implements RequestHandlerInterceptor {

        private int counter = 0;

        @Override
        public void preHandle(UrlMappingRule rule, RequestHandlerResultHolder holder) {
            if (counter > 0) {
                throw new RuntimeException("request intercepter preHandler are executed twice");
            }
            counter++;
        }

        @Override
        public void postHandle(UrlMappingRule rule, RequestHandlerResultHolder holder, ExceptionHandler exceptionHandler) {
            counter--;
            if (counter < 0) {
                throw new RuntimeException("request intercepter postHandler are executed twice");
            }

        }

    }

    public static class CounterInterceptorAdapter implements DeclareInstanceAdapter {

        private CounterInterceptor interceptor = new CounterInterceptor();

        @Override
        public Object asTargetInstance() {
            return interceptor;
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
