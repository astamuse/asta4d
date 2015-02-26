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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jsoup.nodes.Element;
import org.testng.annotations.Test;

import com.astamuse.asta4d.render.ChildReplacer;
import com.astamuse.asta4d.render.GoThroughRenderer;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.test.render.infra.SimpleCase;
import com.astamuse.asta4d.test.render.infra.TimeCalculator;
import com.astamuse.asta4d.util.ElementUtil;

@Test
public class LambdaRenderingTest extends BaseTest {

    public static class TestRender {

        public Renderer removeClassInListRendering() {
            List<Integer> list = IntStream.range(1, 10 + 1).boxed().collect(Collectors.toList());
            return Renderer.create(".item", list, i -> {
                Renderer render = Renderer.create(".x-num", i);
                int idx = (i % 3) + 1;
                render.add(".x-idx-" + idx, "-class", "x-remove");
                render.add(".x-remove", (Object) null);
                return render;
            });
        }

        public Renderer renderableRendering() {
            final Map<String, String> valueMap = new HashMap<>();
            valueMap.put("value", "00");
            Renderer render = Renderer.create("#test", (Element e) -> {
                valueMap.put("value", "xx");
            });
            render.add("#test", () -> {
                return Renderer.create("*", valueMap.get("value"));
            });
            return render;
        }

        public Renderer listTextRendering() {
            Renderer renderer = new GoThroughRenderer();

            List<String> textList = Arrays.asList("a", "b", "c");
            renderer.add("div#test-text", textList.stream());

            List<Long> longList = Arrays.asList(10L, 20L, 30L);
            renderer.add("div#test-long", longList, (index, obj) -> {
                return obj / 10;
            });

            List<Integer> integerList = Arrays.asList(100, 200, 300);
            renderer.add("div#test-integer", integerList.stream().map(obj -> {
                return obj / 10;
            }));

            List<Boolean> booleanList = Arrays.asList(true, false, false);
            renderer.add("div#test-boolean", booleanList, obj -> {
                return obj && true;
            });
            return renderer;
        }

        public Renderer listElementRendering() {
            Renderer renderer = Renderer.create("div#test", IntStream.range(0, 3 + 1).mapToObj(i -> {
                return ElementUtil.parseAsSingle("<span>BBB:" + i + "</span>");
            }));
            return renderer;
        }

        public Renderer listChildReplacing() {
            List<Integer> list = IntStream.range(0, 3 + 1).boxed().collect(Collectors.toList());
            Renderer renderer = Renderer.create("div#test", list, i -> {
                return new ChildReplacer(ElementUtil.parseAsSingle("<strong>BBB:" + i + "</strong>"));
            });
            return renderer;
        }

        public Renderer listRecursiveRendering() {
            List<String[]> list = new ArrayList<>();
            for (int i = 0; i <= 3; i++) {
                String[] sa = { "aa-" + i, "bb-" + i };
                list.add(sa);
            }
            Renderer renderer = Renderer.create("div#test", list, obj -> {
                Renderer r = Renderer.create("#s1", obj[0]);
                r.add("#s2", obj[1]);
                return r;
            });
            return renderer;
        }

        public Renderer parallelStreamRenderingCorrectness() {
            Renderer renderer = new GoThroughRenderer();

            List<String> textList = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
            renderer.add("div#test-text", textList.parallelStream().map(t -> {
                return t;
            }));

            return renderer;
        }

        public Renderer parallelStreamRenderingTimeConsuming() {
            Renderer renderer = new GoThroughRenderer();
            List<String> textList = Arrays.asList("a", "b", "c");
            renderer.add("div#test-text", textList.parallelStream().map(t -> {
                try {
                    Thread.currentThread().sleep(1000);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return t;
            }));

            return renderer;
        }
    }

    public void testRemoveClassInListRendering() throws Throwable {
        new SimpleCase("LambdaRendering_removeClassInListRendering.html");
    }

    public void testRenderableRendering() throws Throwable {
        new SimpleCase("LambdaRendering_renderableRendering.html");
    }

    public void testListElementRendering() throws Throwable {
        new SimpleCase("LambdaRendering_listElementRendering.html");
    }

    public void testListTextRendering() throws Throwable {
        new SimpleCase("LambdaRendering_listTextRendering.html");
    }

    public void testListChildReplacing() throws Throwable {
        new SimpleCase("LambdaRendering_listChildReplacing.html");
    }

    public void testListRecursiveRendering() throws Throwable {
        new SimpleCase("LambdaRendering_listRecursiveRendering.html");
    }

    public void testParallelStreamRenderingCorrectness() throws Throwable {
        // we want to confirm it 10 times
        for (int i = 0; i < 10; i++) {
            new SimpleCase("LambdaRendering_parallelStreamRenderingCorrectness.html");
        }
    }

    public void testParallelStreamRenderingTimeConsuming() throws Throwable {
        // we do not know how many threads will be started for parallel stream by the jvm, so if we render a list with too many elements, we
        // will have no idea about what is the appropriate execution time for multi-thread rendering. Thus we limit the size of target list
        // to 3, which means 2 seconds is a ideal value.
        TimeCalculator.shouldRunInTime(() -> {
            try {
                new SimpleCase("LambdaRendering_parallelStreamRenderingTimeConsuming.html");
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }, 2000);

    }
}
