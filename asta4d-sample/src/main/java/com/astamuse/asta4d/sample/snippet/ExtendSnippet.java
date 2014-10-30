package com.astamuse.asta4d.sample.snippet;

import com.astamuse.asta4d.render.Renderer;

public class ExtendSnippet {
    public Renderer renderTabs(String target) {
        return Renderer.create("#" + target, "+class", "active");
    }
}
