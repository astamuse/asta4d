package com.astamuse.asta4d.test.render;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.astamuse.asta4d.data.concurrent.ParallelDataConvertor;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.test.render.infra.SimpleCase;
import com.astamuse.asta4d.test.render.infra.TimeCalculator;
import com.astamuse.asta4d.util.ElementUtil;

@Test(singleThreaded = true)
public class ParallelTest extends BaseTest {

    public static class TestRender {
        public Renderer listTextRendering() {
            List<String> list = Arrays.asList("a", "b", "c", "d");
            Renderer renderer = Renderer.create("div#test", list, new ParallelDataConvertor<String, String>() {
                @Override
                public String convert(String obj) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return obj + "-sleep";
                }
            });
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
    public void paralleListConversion() {
        TimeCalculator.shouldRunInTime(new Runnable() {
            @Override
            public void run() {
                new SimpleCase("ParallelTest_parallelListConversion.html");
            }
        }, 2000);
    }

    @Test
    public void paralleSnippet() {

        TimeCalculator.shouldRunInTime(new Runnable() {
            @Override
            public void run() {
                new SimpleCase("ParallelTest_parallelSnippet.html");
            }
        }, 2000);

    }
}
