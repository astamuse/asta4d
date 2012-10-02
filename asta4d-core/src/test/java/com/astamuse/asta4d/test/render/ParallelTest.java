package com.astamuse.asta4d.test.render;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.astamuse.asta4d.concurrent.ParallelDataConvertor;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.test.render.infra.SimpleCase;
import com.astamuse.asta4d.test.render.infra.TimeCalculator;

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
}
