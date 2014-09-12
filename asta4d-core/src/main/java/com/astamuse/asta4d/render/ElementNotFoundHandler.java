package com.astamuse.asta4d.render;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.render.transformer.ElementSetterTransformer;

public abstract class ElementNotFoundHandler extends Renderer {

    private final static ElementSetterTransformer DoNothingTransformer = new ElementSetterTransformer(new ElementSetter() {
        @Override
        public void set(Element elem) {
            // do nothing
        }
    });

    public ElementNotFoundHandler(String selector) {
        super(selector, DoNothingTransformer);
    }

    @Override
    RendererType getRendererType() {
        return RendererType.ELEMENT_NOT_FOUND_HANDLER;
    }

    @Override
    public String toString() {
        return "ElementNotFoundHandler";
    }

    public abstract Renderer alternativeRenderer();
}
