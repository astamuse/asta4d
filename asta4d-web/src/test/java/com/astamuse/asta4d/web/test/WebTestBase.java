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
package com.astamuse.asta4d.web.test;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.snippet.resolve.DefaultSnippetResolver;
import com.astamuse.asta4d.template.ClasspathTemplateResolver;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;

@Test
public class WebTestBase {
    private final static WebApplicationConfiguration configuration = new WebApplicationConfiguration() {
        {
            ClasspathTemplateResolver templateResolver = new ClasspathTemplateResolver();
            templateResolver.setSearchPathList("/com/astamuse/asta4d/web/test/render/templates",
                    "/com/astamuse/asta4d/web/test/form/templates");
            this.setTemplateResolver(templateResolver);

            DefaultSnippetResolver snippetResolver = new DefaultSnippetResolver();
            snippetResolver.setSearchPathList("com.astamuse.asta4d.web.test.render", "com.astamuse.asta4d.web.test.form");
            this.setSnippetResolver(snippetResolver);

            this.setOutputAsPrettyPrint(true);

            this.setSaveCallstackInfoOnRendererCreation(true);
        }
    };
    static {
        Configuration.setConfiguration(configuration);
    }

    @BeforeMethod
    public void initContext() {
        Configuration.setConfiguration(configuration);
        Context context = Context.getCurrentThreadContext();
        if (context == null) {
            context = new WebApplicationContext();
            Context.setCurrentThreadContext(context);

        }
        context.init();
    }

    @AfterMethod
    public void clearContext() {
        Context.getCurrentThreadContext().clear();
    }
}
