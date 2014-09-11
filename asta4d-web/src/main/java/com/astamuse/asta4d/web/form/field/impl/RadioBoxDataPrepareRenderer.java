package com.astamuse.asta4d.web.form.field.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.extnode.ExtNodeConstants;
import com.astamuse.asta4d.extnode.GroupNode;
import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.Renderable;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.render.transformer.ElementTransformer;
import com.astamuse.asta4d.util.ElementUtil;
import com.astamuse.asta4d.util.IdGenerator;
import com.astamuse.asta4d.util.SelectorUtil;
import com.astamuse.asta4d.util.collection.RowRenderer;
import com.astamuse.asta4d.web.form.field.AdditionalDataUtil;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;
import com.astamuse.asta4d.web.form.field.SimpleFormFieldAdditionalRenderer;

public class RadioBoxDataPrepareRenderer extends SimpleFormFieldAdditionalRenderer {

    public static final String LABEL_REF_ATTR = Configuration.getConfiguration().getTagNameSpace() + ":" + "label-ref-for-inputbox-id";

    private final static class WrapperIdHolder {
        String inputId = null;
        String wrapperId = null;
        String labelSelector = null;
        List<Element> relocatingLabels = new LinkedList<>();
    }

    private String targetInputElementIdReferenceAttr = null;
    private OptionValueMap optionMap = null;

    public RadioBoxDataPrepareRenderer(Class cls, String fieldName) {
        super(cls, fieldName);
    }

    public RadioBoxDataPrepareRenderer setOptionData(OptionValueMap optionMap) {
        this.optionMap = optionMap;
        return this;
    }

    public RadioBoxDataPrepareRenderer setTargetInputElementIdReferenceAttr(String attrName) {
        this.targetInputElementIdReferenceAttr = attrName;
        return this;
    }

    private static final Renderer DoNothingRenderer = Renderer.create();

    @Override
    public Renderer preRender(final String editSelector, final String displaySelector) {

        Renderer renderer = super.preRender(editSelector, displaySelector);

        // create wrapper for input element
        final WrapperIdHolder wrapperIdHolder = new WrapperIdHolder();

        renderer.addDebugger("before wrapping");

        renderer.add(new Renderer(editSelector, new ElementTransformer(null) {
            @Override
            public Element invoke(Element elem) {

                if (wrapperIdHolder.wrapperId != null) {
                    throw new RuntimeException("The target of selector[" + editSelector +
                            "] must be unique but over than 1 target was found.");
                }

                String id = elem.id();
                if (StringUtils.isEmpty(id)) {
                    throw new RuntimeException("A radio box input element must have id value being configured:" + elem.html());
                }

                GroupNode wrapper = new GroupNode();

                // cheating the rendering engine for not skipping the rendering on group node
                wrapper.attr(ExtNodeConstants.GROUP_NODE_ATTR_TYPE, ExtNodeConstants.GROUP_NODE_ATTR_TYPE_USERDEFINE);

                wrapper.appendChild(ElementUtil.safeClone(elem));

                String wrapperId = IdGenerator.createId();
                wrapper.attr("id", wrapperId);

                wrapperIdHolder.inputId = id;
                wrapperIdHolder.wrapperId = wrapperId;

                if (targetInputElementIdReferenceAttr == null) {
                    wrapperIdHolder.labelSelector = SelectorUtil.attr("label", "for", wrapperIdHolder.inputId);
                } else {
                    wrapperIdHolder.labelSelector = SelectorUtil.attr(targetInputElementIdReferenceAttr, wrapperIdHolder.inputId);
                }

                return wrapper;
            }
        }));

        renderer.addDebugger("before collecting relocating");

        renderer.add(":root", new Renderable() {
            @Override
            public Renderer render() {
                if (wrapperIdHolder.wrapperId == null) {
                    // for display mode?
                    return DoNothingRenderer;
                }

                Renderer renderer = new Renderer(wrapperIdHolder.labelSelector, new ElementTransformer(null) {
                    @Override
                    public Element invoke(Element elem) {
                        wrapperIdHolder.relocatingLabels.add(ElementUtil.safeClone(elem));
                        return new GroupNode();
                    }

                });

                return renderer;
            }
        });

        renderer.addDebugger("before relocating");
        renderer.add(":root", new Renderable() {
            @Override
            public Renderer render() {

                if (wrapperIdHolder.wrapperId == null) {
                    // for display mode?
                    return DoNothingRenderer;
                }

                String selector = SelectorUtil.id(wrapperIdHolder.wrapperId);

                return Renderer.create(selector, new ElementSetter() {
                    @Override
                    public void set(Element elem) {
                        if (wrapperIdHolder.relocatingLabels.isEmpty()) {// no existing label found
                            Element label = new Element(Tag.valueOf("label"), "");
                            label.attr("for", wrapperIdHolder.inputId);
                            elem.appendChild(label);
                        } else {
                            for (Element label : wrapperIdHolder.relocatingLabels) {
                                elem.appendChild(label);
                            }
                        }
                    }
                });

            }
        });

        // here we finished restructure the input element and its related label element and then we begin to manufacture all the input/label
        // pairs for option list

        renderer.addDebugger("before rendering value");
        renderer.add(":root", new Renderable() {
            @Override
            public Renderer render() {
                if (wrapperIdHolder.wrapperId == null) {
                    // for display mode?
                    return DoNothingRenderer;
                }
                String selector = SelectorUtil.id(wrapperIdHolder.wrapperId);
                return Renderer.create(selector, optionMap.getOptionList(), new RowRenderer<OptionValuePair>() {
                    @Override
                    public Renderer convert(int rowIndex, OptionValuePair row) {
                        String inputSelector = SelectorUtil.id("input", wrapperIdHolder.inputId);
                        Renderer renderer = Renderer.create(inputSelector, "value", row.getValue());

                        String newInputId = IdGenerator.createId();
                        renderer.add(wrapperIdHolder.labelSelector, LABEL_REF_ATTR, newInputId);

                        renderer.add(inputSelector, "id", newInputId);
                        renderer.add("label", "for", newInputId);
                        renderer.add("label", row.getDisplayText());
                        return renderer;
                    }
                });
            }
        });

        renderer.addDebugger("after rendering value");

        // even we cheated the rendering engine, we should set the type of group node created to faked for fast clean up
        renderer.add(":root", new Renderable() {
            @Override
            public Renderer render() {
                if (wrapperIdHolder.wrapperId == null) {
                    // for display mode?
                    return DoNothingRenderer;
                }
                String selector = SelectorUtil.id(wrapperIdHolder.wrapperId);
                return Renderer.create(selector, new ElementSetter() {
                    @Override
                    public void set(Element elem) {
                        elem.attr(ExtNodeConstants.GROUP_NODE_ATTR_TYPE, ExtNodeConstants.GROUP_NODE_ATTR_TYPE_FAKE);
                    }
                });
            }
        });

        renderer.addDebugger("after uncheating");

        AdditionalDataUtil.storeDataToContextBySelector(editSelector, displaySelector, optionMap);

        return renderer;
    }

}
