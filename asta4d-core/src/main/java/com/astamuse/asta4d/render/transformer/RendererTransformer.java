package com.astamuse.asta4d.render.transformer;

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

        // add a dummy parent so that the result element can be replaced by the
        // sub renderer.
        GroupNode wrapper = new GroupNode();
        wrapper.appendChild(result);
        RenderUtil.apply(result, content);

        return wrapper;
    }

}
