package com.astamuse.asta4d.template.render;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.template.extnode.ExtNodeConstants;
import com.astamuse.asta4d.template.transformer.ElementSetterTransformer;

public class GoThroughRenderer extends Renderer {

    private final static ElementSetter DoNothingSetter = new ElementSetter() {
        @Override
        public void set(Element elem) {
            // do nothing
        }
    };

    public GoThroughRenderer() {
        super(ExtNodeConstants.GOTHROGH_NODE_TAG, new ElementSetterTransformer(DoNothingSetter));
    }

}
