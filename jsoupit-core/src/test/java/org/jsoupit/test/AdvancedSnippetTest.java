package org.jsoupit.test;

import org.jsoup.nodes.Element;
import org.jsoupit.template.extnode.SnippetNode;
import org.jsoupit.template.render.ChildReplacer;
import org.jsoupit.template.render.Renderer;
import org.jsoupit.template.util.ElementUtil;
import org.jsoupit.test.infra.BaseTest;
import org.jsoupit.test.infra.SimpleCase;

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
            final Element subSnippet = new SnippetNode("AdvancedSnippetTest$TestSnippet:dynamicSnippetSub");
            Renderer render = Renderer.create("#pv", new ChildReplacer(subSnippet));
            return render;
        }

        public Renderer dynamicSnippetSub() {
            Renderer render = Renderer.create("*", ElementUtil.text("ff"));
            return render;
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

    }

}
