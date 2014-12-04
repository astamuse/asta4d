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
package com.astamuse.asta4d.web.test.unit.sitecategory;

import java.util.Locale;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.template.ClasspathTemplateResolver;
import com.astamuse.asta4d.template.Template;
import com.astamuse.asta4d.template.TemplateResolver;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.sitecategory.SiteCategoryAwaredTemplateResolver;
import com.astamuse.asta4d.web.sitecategory.SiteCategoryUtil;

@Test
public class SiteCategoryAwaredTemplateResolverTest {

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

    }

    @DataProvider(name = "data")
    public Object[][] getTestData() throws Exception {
        //@formatter:off
        return new Object[][] { 
                { "testTemplate1.html", "category1:testTemplate1"},
                { "testTemplate2.html", "category1:testTemplate2"},
                { "testTemplate3.html", "category2:testTemplate3"},
                { "testTemplate4.html", "category3:testTemplate4"},
                };
        //@formatter:on
    }

    @Test(dataProvider = "data")
    public void testResolve(String targetFile, String expectedContent) throws Exception {
        SiteCategoryUtil.setCurrentRequestSearchCategories("category1", "category2", "category3");

        SiteCategoryAwaredTemplateResolver resolver = new SiteCategoryAwaredTemplateResolver() {
            @Override
            protected TemplateResolver createUnderlineTemplateResolverInstance(Class<? extends TemplateResolver> cls) throws Exception {
                ClasspathTemplateResolver underLineResolver = new ClasspathTemplateResolver();
                underLineResolver.setSearchPathList("/com/astamuse/asta4d/web/test/unit/sitecategory");
                return underLineResolver;
            }
        };

        Template template = resolver.findTemplate(targetFile);
        Assert.assertEquals(template.getDocumentClone().body().text(), expectedContent);
    }
}
