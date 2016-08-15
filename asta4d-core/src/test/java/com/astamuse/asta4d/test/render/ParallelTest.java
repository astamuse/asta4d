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

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.test.render.infra.SimpleCase;
import com.astamuse.asta4d.test.render.infra.TimeCalculator;
import com.astamuse.asta4d.util.ElementUtil;
import com.astamuse.asta4d.util.collection.RowConvertorBuilder;

@Test(singleThreaded = true)
public class ParallelTest extends BaseTest {

    public static class TestRender {

        public Renderer listTextRenderingLambda() {
            List<String> list = Arrays.asList("a", "b", "c", "d");
            Renderer renderer = Renderer.create("div#test", list, RowConvertorBuilder.parallel(obj -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return obj + "-sleep";
            }));
            return renderer;
        }

        public Renderer snippetInDiv() {
            Renderer renderer = Renderer.create("div", "in div");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return renderer;
        }

        public Renderer snippetReplaceDiv() {
            Renderer renderer = Renderer.create("div", ElementUtil.parseAsSingle("<span>replace to span</span>"));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return renderer;
        }

        public Renderer snippetNormal() {
            Renderer renderer = Renderer.create("div", "+class", "normal");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return renderer;
        }
    }

    @Test
    public void paralleListConversionLambda() {
        TimeCalculator.shouldRunInTime(new Runnable() {
            @Override
            public void run() {
                try {
                    new SimpleCase("ParallelTest_parallelListConversionLambda.html");
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }
        }, 2000);
    }

    @Test
    public void paralleSnippet() {

        TimeCalculator.shouldRunInTime(new Runnable() {
            @Override
            public void run() {
                try {
                    new SimpleCase("ParallelTest_parallelSnippet.html");
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }
        }, 2000);

    }
}
