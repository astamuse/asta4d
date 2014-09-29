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
import com.astamuse.asta4d.util.annotation.AnnotatedPropertyInfo;
import com.astamuse.asta4d.util.collection.RowRenderer;
import com.astamuse.asta4d.web.form.field.AdditionalDataUtil;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;
import com.astamuse.asta4d.web.form.field.SimpleFormFieldAdditionalRenderer;

public class RadioBoxDataPrepareRenderer extends SimpleFormFieldAdditionalRenderer {

    public static final String LABEL_REF_ATTR = Configuration.getConfiguration().getTagNameSpace() + ":" + "label-ref-for-inputbox-id";

    public static final String DUPLICATOR_REF_ID_ATTR = Configuration.getConfiguration().getTagNameSpace() + ":" +
            "input-radio-duplicator-ref-id";

    public static final String DUPLICATOR_REF_ATTR = Configuration.getConfiguration().getTagNameSpace() + ":" +
            "input-radio-duplicator-ref";

    private final static class WrapperIdHolder {
        String inputId = null;
        String wrapperId = null;
        String labelSelector = null;
        List<Element> relocatingLabels = new LinkedList<>();
    }

    private String targetInputElementIdReferenceAttr = null;
    private boolean inputIdByValue = false;
    private String duplicateSelector = null;
    private OptionValueMap optionMap = null;

    @SuppressWarnings("deprecation")
    /**
     * For test purpose
     * @param fieldName
     */
    @Deprecated
    public RadioBoxDataPrepareRenderer(String fieldName) {
        super(fieldName);
    }

    public RadioBoxDataPrepareRenderer(AnnotatedPropertyInfo field) {
        super(field);
    }

    public RadioBoxDataPrepareRenderer(Class cls, String fieldName) {
        super(cls, fieldName);
    }

    public RadioBoxDataPrepareRenderer setOptionData(OptionValueMap optionMap) {
        this.optionMap = optionMap;
        return this;
    }

    /**
     * By default, there must be a label tag which "for" attribute is specified to the against input element, then this prepare renderer
     * will use a select as "label[for=#id]" to retrieve the label element of the input element. <br>
     * User can specify a special attribute name to tell this prepare renderer to use selector as "[attrName=#id]" to retrieve the against
     * label element which may be a label element with some decorating outer parent elements.
     * 
     * 
     * @param attrName
     * @return
     */
    public RadioBoxDataPrepareRenderer setTargetInputElementIdReferenceAttr(String attrName) {
        this.targetInputElementIdReferenceAttr = attrName;
        return this;
    }

    /**
     * This prepare renderer will generate new uuids for duplicated input elements but it make test verification difficult. specify true for
     * inputIdByValue will make the generated id fixed to the test value.
     * <p>
     * <b>NOTE:</b> This method is for test purpose and we do not recommend to use it in normal rendering logic.
     * 
     * @param inputIdByValue
     * @return
     */
    public RadioBoxDataPrepareRenderer setInputIdByValue(boolean inputIdByValue) {
        this.inputIdByValue = inputIdByValue;
        return this;
    }

    /**
     * This prepare renderer will simply duplicate the continuous input/label pair. If the duplicateSelector is specified, the
     * duplicateSelector will be used to duplicate the target element which is assumed to be containing the actual input/label pair.
     * 
     * @param duplicateSelector
     * @return
     */
    public RadioBoxDataPrepareRenderer setDuplicateSelector(String duplicateSelector) {
        this.duplicateSelector = duplicateSelector;
        return this;
    }

    @Override
    public Renderer preRender(final String editSelector, final String displaySelector) {

        Renderer renderer = super.preRender(editSelector, displaySelector);

        // create wrapper for input element
        final WrapperIdHolder wrapperIdHolder = new WrapperIdHolder();

        if (duplicateSelector == null) {

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

                    // put the input element under the wrapper node
                    wrapper.appendChild(ElementUtil.safeClone(elem));

                    String wrapperId = IdGenerator.createId();
                    wrapper.attr("id", wrapperId);

                    wrapperIdHolder.inputId = id;
                    wrapperIdHolder.wrapperId = wrapperId;

                    // record the selector for against label
                    if (targetInputElementIdReferenceAttr == null) {
                        wrapperIdHolder.labelSelector = SelectorUtil.attr("label", "for", wrapperIdHolder.inputId);
                    } else {
                        wrapperIdHolder.labelSelector = SelectorUtil.attr(targetInputElementIdReferenceAttr, wrapperIdHolder.inputId);
                    }

                    return wrapper;
                }
            }));

            renderer.add(":root", new Renderable() {
                @Override
                public Renderer render() {
                    if (wrapperIdHolder.wrapperId == null) {
                        // for display mode?
                        return Renderer.create();
                    }

                    // remove the label element and cache it in warpperIdHolder, we will relocate it later(since we have to duplicate the
                    // input
                    // and label pair by given option value map, we have to make sure that the input and label elements are in same parent
                    // node
                    // which can be duplicated)
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

            renderer.add(":root", new Renderable() {
                @Override
                public Renderer render() {

                    if (wrapperIdHolder.wrapperId == null) {
                        // for display mode?
                        return Renderer.create();
                    }

                    String selector = SelectorUtil.id(wrapperIdHolder.wrapperId);

                    // relocate the label element to the wrapper node
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

        } else {
            // if duplicateSelector is specified, we just only need to store the input element id
            renderer.add(editSelector, new ElementSetter() {
                @Override
                public void set(Element elem) {
                    if (wrapperIdHolder.wrapperId != null) {
                        throw new RuntimeException("The target of selector[" + editSelector +
                                "] must be unique but over than 1 target was found.");
                    }
                    String id = elem.id();
                    if (StringUtils.isEmpty(id)) {
                        throw new RuntimeException("A radio box input element must have id value being configured:" + elem.html());
                    }
                    wrapperIdHolder.inputId = id;

                    // record the selector for against label
                    if (targetInputElementIdReferenceAttr == null) {
                        wrapperIdHolder.labelSelector = SelectorUtil.attr("label", "for", wrapperIdHolder.inputId);
                    } else {
                        wrapperIdHolder.labelSelector = SelectorUtil.attr(targetInputElementIdReferenceAttr, wrapperIdHolder.inputId);
                    }
                }
            });
        }

        // here we finished restructure the input element and its related label element and then we begin to manufacture all the input/label
        // pairs for option list

        renderer.add(":root", new Renderable() {
            @Override
            public Renderer render() {
                if (wrapperIdHolder.wrapperId == null && duplicateSelector == null) {
                    // for display mode?
                    return Renderer.create();
                }
                String selector = duplicateSelector == null ? SelectorUtil.id(wrapperIdHolder.wrapperId) : duplicateSelector;
                return Renderer.create(selector, optionMap.getOptionList(), new RowRenderer<OptionValuePair>() {
                    @Override
                    public Renderer convert(int rowIndex, OptionValuePair row) {
                        String inputSelector = SelectorUtil.id("input", wrapperIdHolder.inputId);
                        Renderer renderer = Renderer.create(inputSelector, "value", row.getValue());

                        // we have to generate a new uuid for the input element to make sure its id is unique even we duplicated it.
                        String newInputId = inputIdByValue ? row.getValue() : IdGenerator.createId();

                        // make the generated id more understandable by prefixing with original id
                        newInputId = wrapperIdHolder.inputId + "-" + newInputId;

                        String duplicatorRef = null;

                        if (duplicateSelector != null) {
                            if (inputIdByValue) {
                                duplicatorRef = "duplicator-ref-" + newInputId;
                            } else {
                                duplicatorRef = IdGenerator.createId();
                            }
                        }

                        renderer.add(":root", DUPLICATOR_REF_ID_ATTR, duplicatorRef);

                        renderer.add(inputSelector, DUPLICATOR_REF_ATTR, duplicatorRef);
                        renderer.add(inputSelector, "id", newInputId);

                        // may be a wrapper container of label
                        renderer.add(wrapperIdHolder.labelSelector, LABEL_REF_ATTR, newInputId);

                        renderer.add(wrapperIdHolder.labelSelector, DUPLICATOR_REF_ATTR, duplicatorRef);

                        renderer.add("label", "for", newInputId);
                        renderer.add("label", row.getDisplayText());
                        return renderer;
                    }
                });
            }
        });

        // since we cheated the rendering engine, we should set the type of group node created to faked for fast clean up
        renderer.add(":root", new Renderable() {
            @Override
            public Renderer render() {
                if (wrapperIdHolder.wrapperId == null) {
                    // for display mode?
                    return Renderer.create();
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

        AdditionalDataUtil.storeDataToContextBySelector(editSelector, displaySelector, optionMap);

        return renderer;
    }
}
