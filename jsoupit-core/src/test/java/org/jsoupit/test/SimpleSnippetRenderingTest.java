package org.jsoupit.test;

import org.jsoup.nodes.Element;
import org.jsoupit.template.render.Renderer;
import org.jsoupit.template.util.HtmlUtil;
import org.jsoupit.test.infra.BaseTest;
import org.jsoupit.test.infra.SimpleCase;

public class SimpleSnippetRenderingTest extends BaseTest {

    public static class TagEmbed {
        public Renderer render() {
            return Renderer.create("span", "wow");
        }
    }

    public void testTagEmbed() {
        new SimpleCase("SimpleSnippet_TagEmbed.html");
    }

    public void testSnippetTag() {
        new SimpleCase("SimpleSnippet_SnippetTag.html");
    }

    public void testBasePackageSnippetSearch() {
        new SimpleCase("SimpleSnippet_BasePackage.html");
    }

    public static class ElementRendering {
        public Renderer replaceElement() {
            Element elem = HtmlUtil.parseHTMLAsSingleElement("<div>i am a danymic element</div>");
            return Renderer.create("*", elem);
        }
    }

    public void testElementRendering() {
        new SimpleCase("SimpleSnippet_elementRendering.html");
    }
}
