package com.astamuse.asta4d.interceptor;

import com.astamuse.asta4d.render.Renderer;

public interface PageInterceptor {

    public boolean prePageRendering(Renderer renderer);

    public void postPageRendering(Renderer renderer);
}
