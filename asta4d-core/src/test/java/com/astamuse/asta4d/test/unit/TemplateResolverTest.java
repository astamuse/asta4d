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

import org.testng.annotations.Test;

import com.astamuse.asta4d.template.AbstractTemplateResolver;
import com.astamuse.asta4d.template.AbstractTemplateResolver.TemplateInfo;
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
}
