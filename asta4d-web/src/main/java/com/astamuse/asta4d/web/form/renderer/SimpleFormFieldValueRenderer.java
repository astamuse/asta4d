package com.astamuse.asta4d.web.form.renderer;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.astamuse.asta4d.extnode.GroupNode;
import com.astamuse.asta4d.render.ElementNotFoundHandler;
import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.Renderable;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.render.transformer.ElementTransformer;
import com.astamuse.asta4d.util.ElementUtil;
import com.astamuse.asta4d.web.form.FormFieldValueRenderer;

public abstract class SimpleFormFieldValueRenderer implements FormFieldValueRenderer {

    private String getNonNullString(Object value) {
        String v = value == null ? "" : value.toString();
        if (v == null) {
            v = "";
        }
        return v;
    }

    @Override
    public final Renderer renderForEdit(String editTargetSelector, Object value) {
        return Renderer.create(editTargetSelector, renderForEdit(getNonNullString(value)));
    }

    @Override
    public Renderer renderForDisplay(String editTargetSelector, String displayTargetSelector, Object value) {
        return renderForDisplay(editTargetSelector, displayTargetSelector, getNonNullString(value));
    }

    protected abstract Renderer renderForEdit(String nonNullString);

    /**
     * 
     * All the sub rendering is delayed by {@link Renderable}.
     * 
     * @param editTargetSelector
     * @param displayTargetSelector
     * @param nonNullString
     * @return
     */
    protected Renderer renderForDisplay(final String editTargetSelector, final String displayTargetSelector, final String nonNullString) {
        // hide the edit element
        Renderer render = Renderer.create(editTargetSelector, new Renderable() {
            @Override
            public Renderer render() {
                return hideTarget(editTargetSelector);
            }
        });

        // render the shown value to target element by displayTargetSelector
        render.add(displayTargetSelector, new Renderable() {

            @Override
            public Renderer render() {
                return renderToDisplayTarget(displayTargetSelector, nonNullString);
            }
        });

        // if the element by displayTargetSelector does not exists, simply add a span to show the value.
        // since ElementNotFoundHandler has been delayed, so the Renderable is not necessary
        render.add(new ElementNotFoundHandler(displayTargetSelector) {
            @Override
            public Renderer alternativeRenderer() {
                return addAlternativeDom(editTargetSelector, nonNullString);
            }
        });
        return render;
    }

    protected Renderer hideTarget(String targetSelector) {
        return Renderer.create(targetSelector, new ElementSetter() {
            @Override
            public void set(Element elem) {
                String style = elem.attr("style");
                if (style != null) {
                    style = style.trim();
                }

                if (StringUtils.isEmpty(style)) {
                    style = "display:none";
                } else {
                    if (style.endsWith(";")) {
                        style = style + "display:none";
                    } else {
                        style = style + ";display:none";
                    }
                }

                elem.attr("style", style);
            }
        });
    }

    protected Renderer renderToDisplayTarget(String displayTargetSelector, String nonNullString) {
        return Renderer.create(displayTargetSelector, nonNullString);
    }

    protected Renderer addAlternativeDom(final String editTargetSelector, final String nonNullString) {

        return new Renderer(editTargetSelector, new ElementTransformer(null) {
            @Override
            public Element invoke(Element elem) {
                GroupNode group = new GroupNode();

                Element editClone = ElementUtil.safeClone(elem);
                group.appendChild(editClone);

                Element newElem = new Element(Tag.valueOf("span"), "");
                newElem.text(nonNullString);
                group.appendChild(newElem);

                return group;
            }

        });

    }

}
