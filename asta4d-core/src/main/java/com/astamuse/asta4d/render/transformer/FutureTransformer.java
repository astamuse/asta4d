package com.astamuse.asta4d.render.transformer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.extnode.GroupNode;

public class FutureTransformer extends Transformer<Future<?>> {

    private final static Logger logger = LoggerFactory.getLogger(FutureTransformer.class);

    public FutureTransformer(Future<?> content) {
        super(content);
    }

    @Override
    protected Element transform(Element elem, Future<?> future) {
        try {
            Object result = future.get();
            Transformer<?> transformer = TransformerFactory.generateTransformer(result);
            return transformer.invoke(elem);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed retrieve result from future:" + e.getMessage(), e);
            return new GroupNode();
        }
    }

}
