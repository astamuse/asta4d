package com.astamuse.asta4d.test;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.render.GoThroughRenderer;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.test.infra.BaseTest;
import com.astamuse.asta4d.test.infra.SimpleCase;
import com.astamuse.asta4d.util.ElementUtil;

public class AdvancedRenderingTest extends BaseTest {

    public static class TestRender {
        public Renderer continualSelectAll() {

            Renderer render = new GoThroughRenderer();

            // replace element
            Element elem = ElementUtil.parseAsSingle("<div>i am a danymic element</div>");
            Renderer repElem = Renderer.create("*", elem);
            repElem.add("*", "replacetext");

            render.add("#d1", repElem);

            Renderer attrSet = Renderer.create("div", "a", "b");
            attrSet.add("#d3[a=b]", "+class", "tc");

            render.add("#d2", attrSet);

            return render;
        }
    }

    public AdvancedRenderingTest() {

    }

    public void testContinualSelectAll() {
        new SimpleCase("AdvancedRendering_continualSelectAll.html");
    }

}
