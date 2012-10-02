package com.astamuse.asta4d.render.transformer;

import java.util.concurrent.Future;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.render.TextSetter;
import com.astamuse.asta4d.util.Asta4DWarningException;

public class TransformerFactory {

    private final static Logger logger = LoggerFactory.getLogger(TransformerFactory.class);

    public final static Transformer<?> generateTransformer(Object action) {
        Transformer<?> transformer;
        if (action instanceof String) {
            transformer = new ElementSetterTransformer(new TextSetter(action.toString()));
        } else if (action instanceof ElementSetter) {
            transformer = new ElementSetterTransformer((ElementSetter) action);
        } else if (action instanceof Renderer) {
            transformer = new RendererTransformer((Renderer) action);
        } else if (action instanceof Future) {
            transformer = new FutureTransformer((Future<?>) action);
        } else if (action instanceof Element) {
            transformer = new ElementTransformer((Element) action);
        } else {
            String msg = "Unsupported type found in transformer generation:" + action.getClass().getName();
            Asta4DWarningException awe = new Asta4DWarningException(msg);
            logger.warn(msg, awe);
            transformer = new ElementSetterTransformer(new TextSetter(action.toString()));
        }
        return transformer;
    }

}
