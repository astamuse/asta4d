package com.astamuse.asta4d.template.transformer;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.template.extnode.GroupNode;
import com.astamuse.asta4d.template.render.Renderer;
import com.astamuse.asta4d.template.util.RenderUtil;

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
