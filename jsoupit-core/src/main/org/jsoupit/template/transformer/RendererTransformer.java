package org.jsoupit.template.transformer;

import org.jsoup.nodes.Element;
import org.jsoupit.template.RenderUtil;
import org.jsoupit.template.extnode.GroupNode;
import org.jsoupit.template.render.Renderer;

public class RendererTransformer extends Transformer<Renderer> {

    public RendererTransformer(Renderer content) {
        super(content);
    }

    @Override
    protected Element transform(Element elem, Renderer content) {
        Element result = elem.clone();
        // TODO there are something that should be rewritten
        GroupNode wrapper = new GroupNode();
        wrapper.appendChild(result);

        RenderUtil.apply(wrapper, content);

        return wrapper;
    }

}
