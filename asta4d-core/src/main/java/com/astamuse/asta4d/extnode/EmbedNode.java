package com.astamuse.asta4d.extnode;

public class EmbedNode extends ExtNode {

    public EmbedNode(String target) {
        super(ExtNodeConstants.EMBED_NODE_TAG);
        this.attr(ExtNodeConstants.EMBED_NODE_ATTR_TARGET, target);
    }

}
