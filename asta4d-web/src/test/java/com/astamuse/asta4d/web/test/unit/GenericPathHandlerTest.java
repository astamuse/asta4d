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

package com.astamuse.asta4d.web.test.unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.template.AbstractTemplateResolver;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.builtin.AbstractGenericPathHandler;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public class GenericPathHandlerTest {

    private static WebApplicationConfiguration configuration = new WebApplicationConfiguration() {
        {
            setTemplateResolver(new AbstractTemplateResolver() {
                @Override
                public TemplateInfo loadResource(String path) {
                    return createTemplateInfo(path, new ByteArrayInputStream(path.getBytes()));
                }
            });
        }
    };

    @BeforeClass
    public void setConf() {
        Locale.setDefault(Locale.ROOT);
        Configuration.setConfiguration(configuration);
    }

    @BeforeTest
    public void setContext() {
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

    @DataProvider(name = "path-convert")
    public Object[][] getPathConvertTestData() throws Exception {
        //@formatter:off
        return new Object[][] { 
                
                {"/**/*", null, "/xxx", "/xxx"},
                //{"/test/**/*", "/test-convert", "/xxx", null},
                {"/test/**/*", "/test-convert", "/test/xxx", "/test-convert/xxx"},
                {"/test/**/*", "/test-convert", "/test/xxx/../yy", null},
                
                {"/test/**/*", "file:/test-convert", "/test/aaa", "file:/test-convert/aaa"},
                
                {"/test/**/*", "file:///test-convert", "/test/bbb", "file:/test-convert/bbb"},
                {"/test/**/*", "file:///C:\\test-convert", "/test/ccc-1", "file:/C:/test-convert/ccc-1"},
                {"/test/**/*", "file://C:\\test-convert", "/test/ccc-2", "file:/C:/test-convert/ccc-2"},
                {"/test/**/*", "file:/C:\\test-convert", "/test/ccc-3", "file:/C:/test-convert/ccc-3"},
                {"/test/**/*", "file:C:\\test-convert", "/test/ccc-4", "file:C:/test-convert/ccc-4"},
                
                {"/test/**/*", "classpath:/test-convert", "/test/ddd", "classpath:/test-convert/ddd"},
        };
        //@formatter:on
    }

    @Test(dataProvider = "path-convert")
    public void testPathConvert(String sourcePattern, String basePath, String url, String expectedPath) {
        WebApplicationContext webContext = WebApplicationContext.getCurrentThreadWebApplicationContext();

        webContext.setAccessURI(url);

        UrlMappingRule rule = mock(UrlMappingRule.class);
        AbstractGenericPathHandler genericPathHandler = new AbstractGenericPathHandler(basePath) {
        };

        when(rule.getSourcePath()).thenReturn(sourcePattern);
        String path = genericPathHandler.convertPath(rule);
        Assert.assertEquals(path, expectedPath);
    }

}
