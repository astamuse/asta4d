package com.astamuse.asta4d.web.form.field.impl;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.astamuse.asta4d.extnode.GroupNode;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.render.transformer.ElementTransformer;
import com.astamuse.asta4d.util.ElementUtil;
import com.astamuse.asta4d.web.form.field.SimpleFormFieldValueRenderer;

public class TextareaRenderer extends SimpleFormFieldValueRenderer {
    @Override
    public Renderer renderForEdit(String nonNullString) {
        return Renderer.create("textarea", nonNullString);
    }

    @Override
    protected Renderer addAlternativeDom(final String editTargetSelector, final String nonNullString) {
        Renderer renderer = Renderer.create();
        renderer.add(new Renderer(editTargetSelector, new ElementTransformer(null) {
            @Override
            public Element invoke(Element elem) {
                GroupNode group = new GroupNode();

                Element editClone = ElementUtil.safeClone(elem);
                group.appendChild(editClone);

                Element newElem = new Element(Tag.valueOf("pre"), "");
                newElem.text(nonNullString);
                group.appendChild(newElem);

                return group;
            }

        }));
        return renderer;
    }

}
