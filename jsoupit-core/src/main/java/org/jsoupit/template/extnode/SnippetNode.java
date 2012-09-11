package org.jsoupit.template.extnode;

public class SnippetNode extends ExtNode {

    public SnippetNode(Class<?> renderClass) {
        this(renderClass.getName());
    }

    public SnippetNode(String renderer) {
        this();
        this.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_RENDER, renderer);
    }

    public SnippetNode() {
        super(ExtNodeConstants.SNIPPET_NODE_TAG);
        this.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS, ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_READY);
    }

}
