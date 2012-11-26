package com.astamuse.asta4d.render;

import org.jsoup.nodes.Element;

/**
 * A ChildReplacer will empty the target Element first, then add the new child
 * node to the target element.
 * 
 * @author e-ryu
 * 
 */
public class ChildReplacer implements ElementSetter {

    private Element newChild;

    /**
     * Constructor
     * 
     * @param newChild
     *            the new child node
     */
    public ChildReplacer(Element newChild) {
        this.newChild = newChild;
    }

    @Override
    public void set(Element elem) {
        elem.empty();
        elem.appendChild(newChild);
    }

    @Override
    public String toString() {
        String s = "replace the children to:{\n" + newChild.toString() + "\n}";
        return s;
    }

}
