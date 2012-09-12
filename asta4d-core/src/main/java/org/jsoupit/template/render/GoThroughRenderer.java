package org.jsoupit.template.render;

import org.jsoup.nodes.Element;
import org.jsoupit.template.extnode.ExtNodeConstants;
import org.jsoupit.template.transformer.ElementSetterTransformer;

public class GoThroughRenderer extends Renderer {

    private final static ElementSetter DoNothingSetter = new ElementSetter() {
        @Override
        public void set(Element elem) {
            // do nothing
        }
    };

    public GoThroughRenderer() {
        super(ExtNodeConstants.GOTHROGH_NODE_TAG, new ElementSetterTransformer(DoNothingSetter));
    }

}
