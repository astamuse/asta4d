package com.astamuse.asta4d.extnode;

/**
 * This node is intended to be used for a variety of purposes, such as combining
 * multi nodes to a single node or being a place holder.
 * 
 * @author e-ryu
 * 
 */
public class GroupNode extends ExtNode {

    public GroupNode() {
        super(ExtNodeConstants.GROUP_NODE_TAG);
        this.attr(ExtNodeConstants.GROUP_NODE_ATTR_TYPE, ExtNodeConstants.GROUP_NODE_ATTR_TYPE_FAKE);
    }
}
