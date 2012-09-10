package org.jsoupit.test;

import org.jsoup.nodes.Element;
import org.jsoupit.template.render.Renderer;
import org.jsoupit.template.util.HtmlUtil;
import org.jsoupit.test.infra.SimpleCase;

public class RenderingTest {

    public static class ElementRendering {
        public Renderer replaceElement() {
            Element elem = HtmlUtil.parseHTMLAsSingleElement("<div>i am a danymic element</div>");
            return Renderer.create("*", elem);
        }
    }

    public void testElementRendering() {
        new SimpleCase("Rendering_elementRendering.html");
    }
}
