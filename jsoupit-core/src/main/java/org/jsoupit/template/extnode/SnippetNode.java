package org.jsoupit.template.extnode;

public class SnippetNode extends ExtNode {

    public SnippetNode(String renderer) {
        this();
        this.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_RENDER, renderer);
    }

    public SnippetNode() {
        super(ExtNodeConstants.SNIPPET_NODE_TAG);
        this.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_FINISHED, "false");
    }

}
