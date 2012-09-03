package org.jsoupit.template.render;

import org.jsoupit.template.extnode.ClearNode;
import org.jsoupit.template.transformer.ElementTransformer;

public class EmptyRenderer extends Renderer {

    public EmptyRenderer() {
        super("*:eq(0)", new ElementTransformer(new ClearNode()));
    }

}
