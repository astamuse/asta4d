package com.astamuse.asta4d.transformer;

import org.jsoup.nodes.Element;

public class TextNodeTransformer extends Transformer<String> {

    public TextNodeTransformer(String content) {
        super(fixContent(content));
    }

    private static String fixContent(String content) {
        // TODO what to do when null?
        return content == null ? "##NULL##" : content;
    }

    @Override
    protected Element transform(Element elem, String content) {
        Element result = elem.clone();
        result.empty();
        result.appendText(content);
        return result;
    }

}
