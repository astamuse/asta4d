package org.jsoupit.test;

import org.jsoup.nodes.Element;
import org.jsoupit.template.render.Renderer;
import org.jsoupit.template.util.ElementUtil;
import org.jsoupit.test.infra.BaseTest;
import org.jsoupit.test.infra.SimpleCase;

public class RenderingTest extends BaseTest {

    public static class ElementRendering {
        public Renderer replaceElement() {
            Element elem = ElementUtil.parseAsSingle("<div>i am a danymic element</div>");
            return Renderer.create("*", elem);
        }
    }

    public void testElementRendering() {
        new SimpleCase("Rendering_elementRendering.html");
    }

    public void testTextRendering() {
        // TODO append text under a node
    }

    public void testNormalAttrSetting() {
        // TODO
        // add attr, remove attr, set attr
    }

    public void testClassAttrSetting() {
        // TODO
        // add class, remove class, set class
    }

    public void testElementSetter() {
        // TODO customized element operation
    }

    public void testClearNode() {
        // TODO ClearNode and ClearRenderer
    }

    public void testChildReplacing() {
        // TODO
    }

    public void testListElementRendering() {
        // TODO
    }

    public void testListTextRendering() {
        // TODO
    }

    public void testListChildReplacing() {
        // TODO
    }

    public void testRecursiveRendering() {
        // TODO
    }

}
