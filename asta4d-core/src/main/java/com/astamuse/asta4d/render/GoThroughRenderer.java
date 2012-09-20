package com.astamuse.asta4d.render;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.transformer.ElementSetterTransformer;

public class GoThroughRenderer extends Renderer {

    private final static ElementSetter DoNothingSetter = new ElementSetter() {
        @Override
        public void set(Element elem) {
            // do nothing
        }
    };

    public GoThroughRenderer() {
        super("", new ElementSetterTransformer(DoNothingSetter));
    }

    @Override
    public String toString() {
        return "GoThroughRenderer";
    }

}
