package com.astamuse.asta4d.transformer;

import org.jsoup.nodes.Element;

public class ElementTransformer extends Transformer<Element> {

    public ElementTransformer(Element content) {
        super(content);
    }

    @Override
    protected Element transform(Element elem, Element content) {
        return content.clone();
    }

}
