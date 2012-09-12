package com.astamuse.asta4d.transformer;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.render.ElementSetter;

public class ElementSetterTransformer extends Transformer<ElementSetter> {

    public ElementSetterTransformer(ElementSetter content) {
        super(content);
    }

    @Override
    public Element transform(Element elem, ElementSetter content) {
        Element result = elem.clone();
        content.set(result);
        return result;
    }

}
