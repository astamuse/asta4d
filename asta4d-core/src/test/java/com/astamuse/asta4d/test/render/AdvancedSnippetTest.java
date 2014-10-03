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

import org.jsoup.nodes.Element;
import org.testng.annotations.Test;

import com.astamuse.asta4d.extnode.EmbedNode;
import com.astamuse.asta4d.extnode.SnippetNode;
import com.astamuse.asta4d.render.ChildReplacer;
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

        public Renderer dynamicSnippetOuter() {
            Element subSnippet = new SnippetNode("AdvancedSnippetTest$TestSnippet:dynamicSnippetSub");
            Renderer render = Renderer.create("#pv", new ChildReplacer(subSnippet));
            return render;
        }

        public Renderer dynamicSnippetSub() {
            Renderer render = Renderer.create("*", ElementUtil.text("ff"));
            return render;
        }

        public Renderer dynamicEmbed() {
            Element embed = new EmbedNode("/AdvancedSnippet_nestedEmbed_include.html");
            embed.attr("value", "6");
            return Renderer.create("#pv", embed);
        }
    }

    public static class ParentSnippet {
        public Renderer render() {
            return Renderer.create("p", "parent");
        }
    }

    public static class MiddleSnippet extends ParentSnippet {

    }

    public static class ChildSnippet extends MiddleSnippet {
        public Renderer render() {
            return Renderer.create("p", "child");
        }
    }

    public AdvancedSnippetTest() {
    }

    public void testDeletedNestedSnippet() {
        new SimpleCase("AdvancedSnippet_deletedNestedSnippet.html");
    }

    public void testNestedSnippet() {
        new SimpleCase("AdvancedSnippet_nestedSnippet.html");
    }

    public void testNestedEmbed() {
        new SimpleCase("AdvancedSnippet_nestedEmbed.html");
    }

    public void testDynamicSnippet() {
        new SimpleCase("AdvancedSnippet_dynamicSnippet.html");
    }

    public void testDynamicEmbed() {
        new SimpleCase("AdvancedSnippet_dynamicEmbed.html");
    }

    public void testOverrideRenderMethod() {
        new SimpleCase("AdvancedSnippet_overrideRenderMethod.html");
    }

}
