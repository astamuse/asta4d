package com.astamuse.asta4d.web.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.astamuse.asta4d.data.ContextBindData;
import com.astamuse.asta4d.interceptor.PageInterceptor;
import com.astamuse.asta4d.render.Renderer;

public class GlobalRenderingInterceptor implements PageInterceptor {

    private final static ContextBindData<Queue<Renderer>> RendererList = new ContextBindData<Queue<Renderer>>(true) {
        @Override
        protected Queue<Renderer> buildData() {
            return new ConcurrentLinkedQueue<>();
        }
    };

    public void addRenderer(Renderer renderer) {
        RendererList.get().add(renderer);
    }

    @Override
    public void prePageRendering(Renderer renderer) {
        // do nothing
    }

    @Override
    public void postPageRendering(Renderer renderer) {
        for (Renderer r : RendererList.get()) {
            renderer.add(r);
        }
    }

}
