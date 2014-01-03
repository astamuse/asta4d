package com.astamuse.asta4d.render;

public abstract class ElementNotFoundHandler extends GoThroughRenderer {

    @Override
    RendererType getRendererType() {
        return RendererType.ELEMENT_NOT_FOUND_HANDLER;
    }

    public abstract Renderer alternativeRenderer();
}
