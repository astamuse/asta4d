package com.astamuse.asta4d.render;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.transformer.ElementSetterTransformer;

public class DebugRenderer extends Renderer {

    private final static String CurrentNodeSelector = "DebugRenderer-CurrentNodeSelector";

    final static Logger logger = LoggerFactory.getLogger(DebugRenderer.class);

    public DebugRenderer() {
        super(CurrentNodeSelector, new ElementSetterTransformer(new ElementSetter() {
            @Override
            public void set(Element elem) {
                logger.debug(elem.toString());
            }
        }));
    }
}
