package com.astamuse.asta4d.template.render;

import com.astamuse.asta4d.template.extnode.ClearNode;
import com.astamuse.asta4d.template.transformer.ElementTransformer;

public class ClearRenderer extends Renderer {

    public ClearRenderer() {
        super("*:eq(0)", new ElementTransformer(new ClearNode()));
    }

}
