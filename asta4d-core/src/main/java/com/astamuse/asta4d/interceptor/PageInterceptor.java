package com.astamuse.asta4d.interceptor;

import com.astamuse.asta4d.render.Renderer;

public interface PageInterceptor {

    public void prePageRendering(Renderer renderer);

    public void postPageRendering(Renderer renderer);
}
