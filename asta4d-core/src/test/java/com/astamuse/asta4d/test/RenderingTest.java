package com.astamuse.asta4d.test;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.render.ChildReplacer;
import com.astamuse.asta4d.render.GoThroughRenderer;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.test.infra.BaseTest;
import com.astamuse.asta4d.test.infra.SimpleCase;
import com.astamuse.asta4d.util.ElementUtil;

public class RenderingTest extends BaseTest {

    public static class TestRender {
        public Renderer elementRendering() {
            Element elem = ElementUtil.parseAsSingle("<div>i am a danymic element</div>");
            Renderer render = Renderer.create("*", elem);
            render.addDebugger();
            return render;
        }

        public Renderer textRendering() {
            Renderer renderer = Renderer.create("div#test", "Prometheus");
            renderer.add("#testspace", "I love this game!");
            renderer.add("#testnbsp", new ChildReplacer(ElementUtil.parseAsSingle("<span>three space here(&nbsp;&nbsp;&nbsp;)</span>")));
            renderer.add("#testgreatermark", "3 > 2 or 3 < 2, it's a question.");
            return renderer;
        }

        public Renderer normalAttrSetting() {
            Renderer renderer = new GoThroughRenderer();
            renderer.add("#testadd", "+v", "2");
            renderer.add("#testaddexisted", "+v", "2");
            renderer.add("#testremovebynull", "v", null);
            renderer.add("#testremovebyminus", "-v", "");
            renderer.add("#testremovebyaddnull", "+v", null);
            renderer.add("#testset", "v", "2");
            return renderer;
        }

        public Renderer classAttrSetting() {
            Renderer renderer = new GoThroughRenderer();
            renderer.add("#testadd", "class", "a");
            renderer.add("#testaddexisted", "+class", "b");
            renderer.add("#testremovebynull", "class", null);
            renderer.add("#testremovebyminus", "-class", "b");
            renderer.add("#testremovebyaddnull", "+class", null);
            renderer.add("#testset", "class", "b");
            return renderer;
        }
    }

    public void testElementRendering() {
        new SimpleCase("Rendering_elementRendering.html");
    }

    public void testTextRendering() {
        new SimpleCase("Rendering_textRendering.html");
    }

    public void testNormalAttrSetting() {
        new SimpleCase("Rendering_normalAttrSetting.html");
    }

    public void testClassAttrSetting() {
        new SimpleCase("Rendering_classAttrSetting.html");
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

    public void testRecursiveRendering() {
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

    public void testListRecursiveRendering() {
        // TODO
    }

    public void testRendererAddOperation() {
        // TODO it should act correctly even though we do not add renderer to
        // the first created render
    }

}
