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

package com.astamuse.asta4d.test.render;

import org.testng.annotations.Test;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.snippet.InitializableSnippet;
import com.astamuse.asta4d.snippet.SnippetInvokeException;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.test.render.infra.SimpleCase;

public class SimpleSnippetRenderingTest extends BaseTest {

    public static class StaticEmbed {
        public Renderer render(String ctype) {
            return Renderer.create("#hellodiv", "statically loaded before snippet being executed");
        }
    }

    public static class TagEmbed {
        public Renderer render(String ctype) {
            return Renderer.create("span", ctype);
        }
    }

    public static class SnippetTag {
        public Renderer render() {
            return Renderer.create("span", "wow");
        }
    }

    public static class InitSnippet implements InitializableSnippet {

        @ContextData
        private String value;

        private long id;

        private String resolvedValue;

        private int count = 0;

        @Override
        public void init() throws SnippetInvokeException {
            resolvedValue = value + "-resolved";
            count++;
        }

        @ContextData
        private void setId(long id) {
            this.id = id;
        }

        public Renderer render_1() {
            return Renderer.create(".value", resolvedValue).add(".count", count);
        }

        public Renderer render_2() {
            return Renderer.create(".value", resolvedValue).add(".count", count);
        }
    }

    public void testTagEmbed() throws Throwable {
        new SimpleCase("SimpleSnippet_TagEmbed.html");
    }

    @Test(enabled = false)
    public void testStaticEmbed() throws Throwable {
        new SimpleCase("SimpleSnippet_StaticEmbed.html");
    }

    public void testSnippetTag() throws Throwable {
        new SimpleCase("SimpleSnippet_SnippetTag.html");
    }

    public void testBasePackageSnippetSearch() throws Throwable {
        new SimpleCase("SimpleSnippet_BasePackage.html");
    }

    public void testSnippetInit() throws Throwable {
        Context.getCurrentThreadContext().setData("value", "fire");
        new SimpleCase("SimpleSnippet_SnippetInit.html");
    }

}
