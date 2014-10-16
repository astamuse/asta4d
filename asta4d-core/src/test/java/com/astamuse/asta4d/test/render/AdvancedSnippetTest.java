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

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import org.testng.annotations.Test;

import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.test.render.infra.SimpleCase;
import com.astamuse.asta4d.util.ElementUtil;

@Test
public class AdvancedSnippetTest extends BaseTest {

    public static class TestSnippet {
        public Renderer nestedSnippet_outer() {
            Renderer render = Renderer.create("#pv", "666");
            render.add("#inner", "value", "777");
            return render;
        }

        public Renderer nestedSnippet_inner(String value) {
            return Renderer.create("#pv", value);
        }

        public Renderer deletedNestedSnippet_outer() {
            return Renderer.create("#inner", Clear);
        }

        public Renderer nestedEmbed(String value) {
            return Renderer.create("#kv", value);
        }

        public Renderer dynamicSnippetSub() {
            Renderer render = Renderer.create("*", ElementUtil.text("ff"));
            return render;
        }

    }

    public static class ParentSnippet {
        public Renderer render() {
            return Renderer.create("p", "parent");
        }

        public Renderer rx(String x) {
            return Renderer.create("p", "parent");
        }
    }

    public static class MiddleSnippet extends ParentSnippet {

    }

    public static class ChildSnippet extends MiddleSnippet {
        public Renderer render() {
            return Renderer.create("p", "child");
        }

        public Renderer rx(@ContextData(name = "xxx") String x) {
            return Renderer.create("p", "child");
        }
    }

    public AdvancedSnippetTest() {
    }

    public void testDeletedNestedSnippet() throws Throwable {
        new SimpleCase("AdvancedSnippet_deletedNestedSnippet.html");
    }

    public void testNestedSnippet() throws Throwable {
        new SimpleCase("AdvancedSnippet_nestedSnippet.html");
    }

    public void testNestedEmbed() throws Throwable {
        new SimpleCase("AdvancedSnippet_nestedEmbed.html");
    }

    public void testOverrideRenderMethod() throws Throwable {
        new SimpleCase("AdvancedSnippet_overrideRenderMethod.html");
    }

}
