package com.astamuse.asta4d.test.render;

import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;

import com.astamuse.asta4d.data.ContextBindData;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.test.render.infra.SimpleCase;

@Test(singleThreaded = true)
public class ContextBindDataTest extends BaseTest {

    private final static AtomicInteger NonParallelRenderCounter = new AtomicInteger();

    public static class NonParallelRender {
        private AtomicInteger coutner = NonParallelRenderCounter;
        private ContextBindData<Integer> data = new ContextBindData<Integer>() {
            @Override
            protected Integer buildData() {
                // should return 1
                return coutner.incrementAndGet();
            }

        };

        public Renderer render() {
            return Renderer.create("*", data.get());
        }
    }

    private final static AtomicInteger ParallelRenderCounter = new AtomicInteger();

    public static class ParallelRender {
        private AtomicInteger counter = ParallelRenderCounter;

        private ContextBindData<Integer> data = new ContextBindData<Integer>(true) {
            @Override
            protected Integer buildData() {
                // should return 1
                return counter.incrementAndGet();
            }

        };

        public Renderer render() {
            return Renderer.create("*", data.get());
        }
    }

    public ContextBindDataTest() {
        // TODO Auto-generated constructor stub
    }

    public void testNonParallel() throws Throwable {
        new SimpleCase("ContextBindData_nonParallel.html");
    }

    public void testParallel() throws Throwable {
        for (int i = 0; i < 20; i++) {
            new SimpleCase("ContextBindData_parallel.html");
        }
    }
}
