package com.astamuse.asta4d.render;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.transformer.ElementSetterTransformer;

public class DebugRenderer extends Renderer {

    final static Logger logger = LoggerFactory.getLogger(DebugRenderer.class);

    public DebugRenderer() {
        super("*:eq(0)", new ElementSetterTransformer(new ElementSetter() {
            @Override
            public void set(Element elem) {
                logger.debug(elem.toString());
            }
        }));
    }
}
