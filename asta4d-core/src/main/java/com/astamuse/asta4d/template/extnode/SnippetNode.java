package com.astamuse.asta4d.template.extnode;

public class SnippetNode extends ExtNode {

    public SnippetNode(Class<?> renderClass) {
        this(renderClass.getName());
    }

    public SnippetNode(String renderer) {
        super(ExtNodeConstants.SNIPPET_NODE_TAG);
        this.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS, ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_READY);
        this.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_RENDER, renderer);
    }

}
