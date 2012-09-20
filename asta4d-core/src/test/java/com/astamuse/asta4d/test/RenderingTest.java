package com.astamuse.asta4d.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.data.DataConvertor;
import com.astamuse.asta4d.extnode.ClearNode;
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

        public Renderer clearNode() {
            Renderer render = Renderer.create("#byClearNode", new ClearNode());
            render.addDebugger();
            render.addDebugger();
            return render;
        }

        public Renderer childReplacing() {
            Element elem = ElementUtil.parseAsSingle("<div>i am a danymic element</div>");
            Renderer render = Renderer.create("span", new ChildReplacer(elem));
            return render;
        }

        public Renderer recursiveRendering() {
            Renderer spanRender = Renderer.create("span#s1", "wow!");
            Renderer divRender = Renderer.create("#d1", spanRender);
            return divRender;
        }

        public Renderer listTextRendering() {
            List<String> list = Arrays.asList("a", "b", "c");
            Renderer renderer = Renderer.create("div#test", list);
            return renderer;
        }

        public Renderer listElementRendering() {
            List<Element> list = new ArrayList<>();
            for (int i = 0; i <= 3; i++) {
                Element elem = ElementUtil.parseAsSingle("<span>BBB:" + i + "</span>");
                list.add(elem);
            }
            Renderer renderer = Renderer.create("div#test", list);
            return renderer;
        }

        public Renderer listChildReplacing() {
            List<Element> list = new ArrayList<>();
            for (int i = 0; i <= 3; i++) {
                Element elem = ElementUtil.parseAsSingle("<strong>BBB:" + i + "</strong>");
                list.add(elem);
            }
            Renderer renderer = Renderer.create("div#test", list, new DataConvertor<Element, ChildReplacer>() {
                @Override
                public ChildReplacer convert(Element obj) {
                    return new ChildReplacer(obj);
                }
            });
            return renderer;
        }

        public Renderer listRecursiveRendering() {
            List<String[]> list = new ArrayList<>();
            for (int i = 0; i <= 3; i++) {
                String[] sa = { "aa-" + i, "bb-" + i };
                list.add(sa);
            }
            Renderer renderer = Renderer.create("div#test", list, new DataConvertor<String[], Renderer>() {

                @Override
                public Renderer convert(String[] obj) {
                    Renderer r = Renderer.create("#s1", obj[0]);
                    r.add("#s2", obj[1]);
                    return r;
                }

            });
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

    /*
        we do not need to test Element setter because there are several renderers extending from it.
        public void testElementSetter() {
            new SimpleCase("Rendering_elementSetter.html");
        }
    */
    public void testClearNode() {
        new SimpleCase("Rendering_clearNode.html");
    }

    public void testChildReplacing() {
        new SimpleCase("Rendering_childReplacing.html");
    }

    public void testRecursiveRendering() {
        new SimpleCase("Rendering_recursiveRendering.html");
    }

    public void testListElementRendering() {
        new SimpleCase("Rendering_listElementRendering.html");
    }

    public void testListTextRendering() {
        new SimpleCase("Rendering_listTextRendering.html");
    }

    public void testListChildReplacing() {
        new SimpleCase("Rendering_listChildReplacing.html");
    }

    public void testListRecursiveRendering() {
        new SimpleCase("Rendering_listRecursiveRendering.html");
    }

    public void testRendererAddOperation() {
        // TODO it should act correctly even though we do not add renderer to
        // the first created render
    }

}
