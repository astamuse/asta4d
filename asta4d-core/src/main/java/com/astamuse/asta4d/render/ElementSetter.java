package com.astamuse.asta4d.render;

import org.jsoup.nodes.Element;

/**
 * An ElementSetter is to be used to reset an element, including attribute
 * setting and any child nodes operations.
 * 
 * @author e-ryu
 * 
 */
public interface ElementSetter {

    /**
     * reset the passed element
     * 
     * @param elem
     *            target element
     */
    public void set(Element elem);
}
