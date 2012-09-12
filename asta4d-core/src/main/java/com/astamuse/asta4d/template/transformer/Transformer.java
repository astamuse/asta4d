package com.astamuse.asta4d.template.transformer;

import org.jsoup.nodes.Element;

public abstract class Transformer<T> {

    private T content;

    public Transformer(T content) {
        this.content = content;
    }

    public Element invoke(Element elem) {
        return transform(elem, content);
    }

    protected abstract Element transform(Element elem, T content);
}
