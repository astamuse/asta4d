/*
 * Copyright 2014 astamuse company,Ltd.
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
package com.astamuse.asta4d.web.test.unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.builtin.StaticResourceHandler;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;
import com.astamuse.asta4d.web.dispatch.response.provider.BinaryDataProvider;

@Test
public class StaticResourceHandlerTest {

    @BeforeClass
    public void setConf() {
        Locale.setDefault(Locale.ROOT);
        WebApplicationConfiguration conf = new WebApplicationConfiguration();
        conf.setCacheEnable(false);
        Configuration.setConfiguration(conf);
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

    @DataProvider(name = "localeSearch")
    public Object[][] getTestData() throws Exception {
        //@formatter:off
        return new Object[][] { 
                { null, "StaticResourceHandlerTestFile"},
                { Locale.JAPANESE, "StaticResourceHandlerTestFile_ja"},
                };
        //@formatter:on
    }

    @Test(dataProvider = "localeSearch")
    public void testLocaleAwareSearch(Locale locale, String expectedContent) throws Exception {
        StaticResourceHandler handler = new StaticResourceHandler(
                "classpath:/com/astamuse/asta4d/web/test/unit/StaticResourceHandlerTestFile.js");
        UrlMappingRule rule = new UrlMappingRule();
        rule.setSourcePath("/StaticResourceHandlerTestFile.js");

        WebApplicationContext context = WebApplicationContext.getCurrentThreadWebApplicationContext();
        context.setAccessURI("/StaticResourceHandlerTestFile.js");
        context.setCurrentLocale(locale);

        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        BinaryDataProvider bdp = (BinaryDataProvider) handler.handler(request, response, null, rule);

        final ByteArrayOutputStream responseBos = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                responseBos.write(b);
            }
        });

        bdp.produce(rule, response);

        Assert.assertEquals(new String(responseBos.toByteArray()), expectedContent);

    }
}
