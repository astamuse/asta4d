package com.astamuse.asta4d.template.render;

import org.jsoup.nodes.Element;

public class ChildReplacer implements ElementSetter {

    private Element newChild;

    public ChildReplacer(Element newChild) {
        this.newChild = newChild;
    }

    @Override
    public void set(Element elem) {
        elem.empty();
        elem.appendChild(newChild);
    }

}
