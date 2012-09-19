package com.astamuse.asta4d.transformer;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.extnode.GroupNode;
import com.astamuse.asta4d.render.RenderUtil;
import com.astamuse.asta4d.render.Renderer;

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

        // TODO we should pass the original cloned element!!!
        RenderUtil.apply(wrapper, content);

        return wrapper;
    }

}
