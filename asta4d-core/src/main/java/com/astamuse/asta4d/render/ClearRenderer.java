package com.astamuse.asta4d.render;

import com.astamuse.asta4d.extnode.ClearNode;
import com.astamuse.asta4d.transformer.ElementTransformer;

public class ClearRenderer extends Renderer {

    public ClearRenderer() {
        super("*:eq(0)", new ElementTransformer(new ClearNode()));
    }

    @Override
    public String toString() {
        return "ClearRenderer";
    }
}
