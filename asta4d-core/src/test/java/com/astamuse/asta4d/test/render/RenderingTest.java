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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.render.ChildReplacer;
import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.GoThroughRenderer;
import com.astamuse.asta4d.render.Renderable;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.test.render.infra.SimpleCase;
import com.astamuse.asta4d.util.ElementUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;

public class RenderingTest extends BaseTest {

    public static class TestRender {
        public Renderer elementRendering() {
            Element elem = ElementUtil.parseAsSingle("<div>i am a danymic element</div>");
            Renderer render = Renderer.create("*", elem);
            render.addDebugger("TestRender");
            return render;
        }

        public Renderer textRendering() {
            Renderer renderer = Renderer.create("div#test", "Prometheus");
            renderer.add("#testspace", "I love this game!");
            renderer.add("#testnbsp", new ChildReplacer(ElementUtil.parseAsSingle("<span>three space here(&nbsp;&nbsp;&nbsp;)</span>")));
            renderer.add("#testgreatermark", "3 > 2 or 3 < 2, it's a question.");
            return renderer;
        }

        public Renderer pseudoRootRendering() {
            Renderer renderer = Renderer.create(":root", "rendering at pseudo root");
            return renderer;
        }

        public Renderer normalAttrSetting() {
            Renderer renderer = new GoThroughRenderer();
            renderer.add("#testadd", "+v", "2");
            renderer.add("#testaddexisted", "+v", "2");
            renderer.add("#testremovebynull", "v", (Object) null);
            renderer.add("#testremovebyClear", "v", Clear);
            renderer.add("#testremovebyminus", "-v", "");
            renderer.add("#testremovebyaddnull", "+v", (Object) null);
            renderer.add("#testremovebyaddClear", "+v", Clear);
            renderer.add("#testset", "v", "2");
            return renderer;
        }

        public Renderer classAttrSetting() {
            Renderer renderer = new GoThroughRenderer();
            renderer.add("#testadd", "class", "a");
            renderer.add("#testaddexisted", "+class", "b");
            renderer.add("#testremovebynull", "class", (Object) null);
            renderer.add(".xabc", "should not be rendered");
            renderer.add("#testremovebyminus", "-class", "b");
            renderer.add("#testremovebyaddnull", "+class", (Object) null);
            renderer.add("#testset", "class", "b");
            return renderer;
        }

        public Renderer removeClassInListRendering() {
            List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            return Renderer.create(".item", list, new RowConvertor<Integer, Renderer>() {
                @Override
                public Renderer convert(int rowIndex, Integer i) {
                    Renderer render = Renderer.create(".x-num", i);
                    int idx = (i % 3) + 1;
                    render.add(".x-idx-" + idx, "-class", "x-remove");
                    render.add(".x-remove", (Object) null);
                    return render;
                }
            });
        }

        public Renderer clearNode() {
            Renderer render = Renderer.create("#byClearRenderer", Clear);

            render.add("#byNullObj", (Object) null);

            render.add("#byNullString", (String) null);

            render.add("#byClearRenderer2", 1 == 1 ? Clear : "XX");

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

        public Renderer renderableRendering() {
            final Map<String, String> valueMap = new HashMap<>();
            valueMap.put("value", "00");
            Renderer render = Renderer.create("#test", new ElementSetter() {
                @Override
                public void set(Element elem) {
                    valueMap.put("value", "xx");
                    // do nothing
                }
            });
            render.add("#test", new Renderable() {
                @Override
                public Renderer render() {
                    return Renderer.create("*", valueMap.get("value"));
                }
            });
            return render;
        }

        public Renderer listTextRendering() {
            Renderer renderer = new GoThroughRenderer();
            // there was a bug when a selector was not found in list rendering
            renderer.add("#not-exists-element", "I love this game!");

            List<String> textList = Arrays.asList("a", "b", "c");
            renderer.add("div#test-text", textList);

            List<Long> longList = Arrays.asList(1L, 2L, 3L);
            renderer.add("div#test-long", longList);

            List<Integer> integerList = Arrays.asList(10, 20, 30);
            renderer.add("div#test-integer", integerList);

            List<Boolean> booleanList = Arrays.asList(true, false, false);
            renderer.add("div#test-boolean", booleanList);
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
            Renderer renderer = Renderer.create("div#test", list, new RowConvertor<Element, ChildReplacer>() {
                @Override
                public ChildReplacer convert(int rowIdx, Element obj) {
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
            Renderer renderer = Renderer.create("div#test", list, new RowConvertor<String[], Renderer>() {

                @Override
                public Renderer convert(int rowIdx, String[] obj) {
                    Renderer r = Renderer.create("#s1", obj[0]);
                    r.add("#s2", obj[1]);
                    return r;
                }

            });
            return renderer;
        }

        public Renderer addOperation() {
            Renderer r1 = Renderer.create("#s1", "r-1");
            Renderer r2 = Renderer.create("#s2", "r-2");
            Renderer r3 = Renderer.create("#s3", "r-3");
            r1.add(r2);
            r2.add(r3);
            return r2;
        }
    }

    public void testElementRendering() throws Throwable {
        new SimpleCase("Rendering_elementRendering.html");
    }

    public void testTextRendering() throws Throwable {
        new SimpleCase("Rendering_textRendering.html");
    }

    public void testPseudoRootRendering() throws Throwable {
        new SimpleCase("Rendering_pseudoRootRendering.html");
    }

    public void testNormalAttrSetting() throws Throwable {
        new SimpleCase("Rendering_normalAttrSetting.html");
    }

    public void testClassAttrSetting() throws Throwable {
        new SimpleCase("Rendering_classAttrSetting.html");
    }

    public void testRemoveClassInListRendering() throws Throwable {
        // TODO to resovle this problem
        new SimpleCase("Rendering_removeClassInListRendering.html");
    }

    /*
        we do not need to test Element setter because there are several renderers extending from it.
        public void testElementSetter() {
            new SimpleCase("Rendering_elementSetter.html");
        }
    */
    public void testClearNode() throws Throwable {
        new SimpleCase("Rendering_clearNode.html");
    }

    public void testChildReplacing() throws Throwable {
        new SimpleCase("Rendering_childReplacing.html");
    }

    public void testRecursiveRendering() throws Throwable {
        new SimpleCase("Rendering_recursiveRendering.html");
    }

    public void testRenderableRendering() throws Throwable {
        new SimpleCase("Rendering_renderableRendering.html");
    }

    public void testListElementRendering() throws Throwable {
        new SimpleCase("Rendering_listElementRendering.html");
    }

    public void testListTextRendering() throws Throwable {
        new SimpleCase("Rendering_listTextRendering.html");
    }

    public void testListChildReplacing() throws Throwable {
        new SimpleCase("Rendering_listChildReplacing.html");
    }

    public void testListRecursiveRendering() throws Throwable {
        new SimpleCase("Rendering_listRecursiveRendering.html");
    }

    public void testRendererAddOperation() throws Throwable {
        // it should act correctly even though we do not add renderer to
        // the first created render and do not return the head renderer
        new SimpleCase("Rendering_addOperation.html");
    }

}
