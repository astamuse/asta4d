package org.jsoupit.template.render;

import org.jsoupit.template.extnode.ClearNode;
import org.jsoupit.template.transformer.ElementTransformer;

public class ClearRenderer extends Renderer {

    public ClearRenderer() {
        super("*:eq(0)", new ElementTransformer(new ClearNode()));
    }

}
