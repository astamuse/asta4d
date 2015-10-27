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

package com.astamuse.asta4d.test.unit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.astamuse.asta4d.template.AbstractTemplateResolver;
import com.astamuse.asta4d.template.Template;
import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.template.TemplateNotFoundException;
import com.astamuse.asta4d.test.render.infra.BaseTest;

public class TemplateResolverTest extends BaseTest {

    @Test(expectedExceptions = TemplateNotFoundException.class)
    public void templateNotFoundTest() throws TemplateException, TemplateNotFoundException {
        AbstractTemplateResolver resolver = new AbstractTemplateResolver() {
            @Override
            protected TemplateInfo loadResource(String name) {
                return null;
            }

        };
        Template template = resolver.findTemplate("test");
    }

    private static InputStream createInputStream(String bodyContent) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>").append(bodyContent).append("</body></html>");
        ByteArrayInputStream bis = new ByteArrayInputStream(sb.toString().getBytes());
        return bis;
    }

    public void templateCacheNonStatic() throws TemplateException, TemplateNotFoundException {

        AbstractTemplateResolver resolver1 = new AbstractTemplateResolver() {
            @Override
            protected TemplateInfo loadResource(String name) {
                return new TemplateInfo("p1", createInputStream("p1"));
            }

        };

        AbstractTemplateResolver resolver2 = new AbstractTemplateResolver() {
            @Override
            protected TemplateInfo loadResource(String name) {
                return new TemplateInfo("p2", createInputStream("p2"));
            }

        };

        Template template1 = resolver1.findTemplate("pp");
        Template template2 = resolver2.findTemplate("pp");

        Assert.assertNotSame(template1, template2);
        Assert.assertNotEquals(template1.getPath(), template2.getPath(), "should be different template path");

    }
}
