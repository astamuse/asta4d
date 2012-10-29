package com.astamuse.asta4d.sample.interceptor;

import com.astamuse.asta4d.interceptor.PageInterceptor;
import com.astamuse.asta4d.render.Renderer;

public class SamplePageInterceptor implements PageInterceptor {

    @Override
    public void prePageRendering(Renderer renderer) {
        System.out.println("[SamplePageInterceptor:prePageRendering]");
    }

    @Override
    public void postPageRendering(Renderer renderer) {
        System.out.println("[SamplePageInterceptor:postPageRendering]");
    }
}
