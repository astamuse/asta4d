package com.astamuse.asta4d.test;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.template.extnode.EmbedNode;
import com.astamuse.asta4d.template.extnode.SnippetNode;
import com.astamuse.asta4d.template.render.ChildReplacer;
import com.astamuse.asta4d.template.render.Renderer;
import com.astamuse.asta4d.template.util.ElementUtil;
import com.astamuse.asta4d.test.infra.BaseTest;
import com.astamuse.asta4d.test.infra.SimpleCase;

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

    public AdvancedSnippetTest() {
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

}
