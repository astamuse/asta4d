package com.astamuse.asta4d.extnode;

/**
 * This Node is intended to be used for dynamically creating snippet. It can be
 * created by a snippet Class information or a plain text renderer declaration.
 * 
 * @author e-ryu
 * 
 */
public class SnippetNode extends ExtNode {

    /**
     * Constructor
     * 
     * @param renderClass
     *            a snippet class
     */
    public SnippetNode(Class<?> renderClass) {
        this(renderClass.getName());
    }

    /**
     * 
     * @param renderer
     *            a plain text renderer declaration
     */
    public SnippetNode(String renderer) {
        super(ExtNodeConstants.SNIPPET_NODE_TAG);
        this.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS, ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_READY);
        this.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_RENDER, renderer);
    }

}
