package org.jsoupit.test;

import org.jsoup.nodes.Element;
import org.jsoupit.template.render.Renderer;
import org.jsoupit.template.util.ElementUtil;
import org.jsoupit.test.infra.SimpleCase;

public class RenderingTest {

    public static class ElementRendering {
        public Renderer replaceElement() {
            Element elem = ElementUtil.parseAsSingle("<div>i am a danymic element</div>");
            return Renderer.create("*", elem);
        }
    }

    public void testElementRendering() {
        new SimpleCase("Rendering_elementRendering.html");
    }

    // TODO test
    /* 1. replace node for text (not add text inner)
     * 2. use "* *" to append node inner (maybe we do not support)
     */
}
