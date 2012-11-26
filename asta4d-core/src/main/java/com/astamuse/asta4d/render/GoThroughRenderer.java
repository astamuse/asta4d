package com.astamuse.asta4d.render;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.render.transformer.ElementSetterTransformer;

/**
 * This Renderer will do nothing, the Rendering process will jump over a render
 * if it is a GoThroughRenderer.
 * 
 * @author e-ryu
 * 
 */
public class GoThroughRenderer extends Renderer {

    private final static ElementSetterTransformer DoNothingTransformer = new ElementSetterTransformer(new ElementSetter() {
        @Override
        public void set(Element elem) {
            // do nothing
        }
    });

    public GoThroughRenderer() {
        super("", DoNothingTransformer);
    }

    @Override
    public String toString() {
        return "GoThroughRenderer";
    }

}
