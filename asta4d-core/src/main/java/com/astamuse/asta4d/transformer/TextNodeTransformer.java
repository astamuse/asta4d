package com.astamuse.asta4d.transformer;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextNodeTransformer extends Transformer<String> {

    private final static Logger logger = LoggerFactory.getLogger(TextNodeTransformer.class);

    public TextNodeTransformer(String content) {
        super(fixContent(content));
    }

    private final static String fixContent(String content) {
        if (content == null) {
            String msg = "Trying to render a null String";
            // we want to get a information of where the null is passed, so we
            // create a exception to get the calling stacks
            Exception ex = new RuntimeException(msg);
            logger.warn(msg, ex);
        }

        return content == null ? "" : content;
    }

    @Override
    protected Element transform(Element elem, String content) {
        Element result = elem.clone();
        result.empty();
        result.appendText(content);
        return result;
    }

}
