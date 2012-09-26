package com.astamuse.asta4d.render;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.util.Asta4DWarningException;

public class TextSetter implements ElementSetter {

    private final static Logger logger = LoggerFactory.getLogger(TextSetter.class);

    private String text;

    public TextSetter(String text) {
        this.text = fixContent(text);
    }

    private final static String fixContent(String content) {
        if (content == null) {
            String msg = "Trying to render a null String";
            // we want to get a information of where the null is passed, so we
            // create a exception to get the calling stacks
            Exception ex = new Asta4DWarningException(msg);
            logger.warn(msg, ex);
        }

        return content == null ? "" : content;
    }

    @Override
    public void set(Element elem) {
        elem.empty();
        elem.appendText(text);
    }

    @Override
    public String toString() {
        return text;
    }

}
