package com.astamuse.asta4d.render.concurrent;

import com.astamuse.asta4d.render.Renderer;

public class FutureRendererHolder {

    String snippetRefId;
    Renderer renderer;

    public FutureRendererHolder(String snippetRefId, Renderer renderer) {
        super();
        this.snippetRefId = snippetRefId;
        this.renderer = renderer;
    }

    public String getSnippetRefId() {
        return snippetRefId;
    }

    public void setSnippetRefId(String snippetRefId) {
        this.snippetRefId = snippetRefId;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

}
