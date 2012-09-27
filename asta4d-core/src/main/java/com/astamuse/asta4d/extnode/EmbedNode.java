package com.astamuse.asta4d.extnode;

/**
 * This Node is intended to be used for dynamically embeding.
 * 
 * @author e-ryu
 * 
 */
public class EmbedNode extends ExtNode {

    /**
     * Constructor
     * 
     * @param target
     *            the target template path
     */
    public EmbedNode(String target) {
        super(ExtNodeConstants.EMBED_NODE_TAG);
        this.attr(ExtNodeConstants.EMBED_NODE_ATTR_TARGET, target);
    }

}
