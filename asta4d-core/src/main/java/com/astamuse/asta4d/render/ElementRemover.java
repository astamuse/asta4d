package com.astamuse.asta4d.render;

import com.astamuse.asta4d.extnode.GroupNode;
import com.astamuse.asta4d.render.transformer.ElementTransformer;

public class ElementRemover extends ElementTransformer {

    public ElementRemover() {
        super(new GroupNode());
    }

}
