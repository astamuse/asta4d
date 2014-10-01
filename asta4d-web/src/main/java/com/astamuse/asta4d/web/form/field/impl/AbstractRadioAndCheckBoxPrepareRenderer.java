package com.astamuse.asta4d.web.form.field.impl;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

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
import com.astamuse.asta4d.web.form.field.PrepareRenderingDataUtil;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;
import com.astamuse.asta4d.web.form.field.SimpleFormFieldPrepareRenderer;

@SuppressWarnings("rawtypes")
public abstract class AbstractRadioAndCheckboxPrepareRenderer<T extends AbstractRadioAndCheckboxPrepareRenderer> extends
        SimpleFormFieldPrepareRenderer {

    public static final String LABEL_REF_ATTR = Configuration.getConfiguration().getTagNameSpace() + ":" + "label-ref-for-inputbox-id";

    public static final String DUPLICATOR_REF_ID_ATTR = Configuration.getConfiguration().getTagNameSpace() + ":" +
            "input-radioandcheck-duplicator-ref-id";

    public static final String DUPLICATOR_REF_ATTR = Configuration.getConfiguration().getTagNameSpace() + ":" +
            "input-radioandcheck-duplicator-ref";

    private final static class WrapperIdHolder {
        String inputId = null;
        String wrapperId = null;
        String labelSelector = null;
        List<Element> relocatingLabels = new LinkedList<>();
    }

    private String labelWrapperIndicatorAttr = null;
    private boolean inputIdByValue = false;
    private String duplicateSelector = null;
    private OptionValueMap optionMap = null;

    /**
     * For test purpose
     * 
     * @param fieldName
     */
    @Deprecated
    public AbstractRadioAndCheckboxPrepareRenderer(String fieldName) {
        super(fieldName);
    }

    public AbstractRadioAndCheckboxPrepareRenderer(AnnotatedPropertyInfo field) {
        super(field);
    }

    public AbstractRadioAndCheckboxPrepareRenderer(Class cls, String fieldName) {
        super(cls, fieldName);
    }

    @SuppressWarnings("unchecked")
    public T setOptionData(OptionValueMap optionMap) {
        this.optionMap = optionMap;
        return (T) this;
    }

    /**
     * for log purpose, "radio" or "checkbox" is expected.
     * 
     * @return
     */
    protected abstract String getTypeString();

    /**
     * By default, there must be a label tag which "for" attribute is specified to the against input element, then this prepare renderer
     * will use a select as "label[for=id]" to retrieve the label element of the input element. <br>
     * User can specify a special attribute name to tell this prepare renderer to use selector as "[attrName=id]" to retrieve the against
     * label element which may be a label element with some decorating outer parent elements.
     * 
     * 
     * @param attrName
     * @return
     */
    @SuppressWarnings("unchecked")
    public T setLabelWrapperIndicatorAttr(String attrName) {
        this.labelWrapperIndicatorAttr = attrName;
        return (T) this;
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
    @SuppressWarnings("unchecked")
    public T setInputIdByValue(boolean inputIdByValue) {
        this.inputIdByValue = inputIdByValue;
        return (T) this;
    }

    /**
     * This prepare renderer will simply duplicate the continuous input/label pair. If the duplicateSelector is specified, the
     * duplicateSelector will be used to duplicate the target element which is assumed to be containing the actual input/label pair.
     * 
     * @param duplicateSelector
     * @return
     */
    @SuppressWarnings("unchecked")
    public T setDuplicateSelector(String duplicateSelector) {
        this.duplicateSelector = duplicateSelector;
        return (T) this;
    }

    @Override
    public Renderer preRender(final String editSelector, final String displaySelector) {

        if (duplicateSelector != null && labelWrapperIndicatorAttr != null) {
            String msg = "duplicateSelector (%s) and labelWrapperIndicatorAttr (%s) cannot be specified at same time.";
            throw new IllegalArgumentException(String.format(msg, duplicateSelector, labelWrapperIndicatorAttr));
        }

        Renderer renderer = super.preRender(editSelector, displaySelector);

        // create wrapper for input element
        final WrapperIdHolder wrapperIdHolder = new WrapperIdHolder();

        if (duplicateSelector == null && optionMap != null) {

            renderer.add(new Renderer(editSelector, new ElementTransformer(null) {
                @Override
                public Element invoke(Element elem) {

                    if (wrapperIdHolder.wrapperId != null) {
                        throw new RuntimeException("The target of selector[" + editSelector +
                                "] must be unique but over than 1 target was found." +
                                "Perhaps you have specified an option value map on a group of elements " +
                                "which is intented to be treated as predefined static options by html directly.");
                    }

                    String id = elem.id();
                    if (StringUtils.isEmpty(id)) {
                        String msg = "A %s input element must have id value being configured:%s";
                        throw new RuntimeException(String.format(msg, getTypeString(), elem.outerHtml()));
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
                    if (labelWrapperIndicatorAttr == null) {
                        wrapperIdHolder.labelSelector = SelectorUtil.attr("label", "for", wrapperIdHolder.inputId);
                    } else {
                        wrapperIdHolder.labelSelector = SelectorUtil.attr(labelWrapperIndicatorAttr, wrapperIdHolder.inputId);
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
            if (duplicateSelector != null && optionMap != null) {
                // if duplicateSelector is specified, we just only need to store the input element id
                renderer.add(editSelector, new ElementSetter() {
                    @Override
                    public void set(Element elem) {
                        if (wrapperIdHolder.inputId != null) {
                            String msg = "The target of selector[%s] (inside duplicator:%s) must be unique but over than 1 target was found.";
                            throw new RuntimeException(String.format(msg, editSelector, duplicateSelector));
                        }
                        String id = elem.id();
                        if (StringUtils.isEmpty(id)) {
                            String msg = "A %s input element (inside duplicator:%s) must have id value being configured:%s";
                            throw new RuntimeException(String.format(msg, getTypeString(), duplicateSelector, elem.outerHtml()));
                        }
                        wrapperIdHolder.inputId = id;

                        // record the selector for against label
                        // labelWrapperIndicatorAttr would not be null since we checked it at the entry of this method.
                        wrapperIdHolder.labelSelector = SelectorUtil.attr("label", "for", wrapperIdHolder.inputId);
                    }
                });
            }
        }

        // here we finished restructure the input element and its related label element and then we begin to manufacture all the input/label
        // pairs for option list

        renderer.add(":root", new Renderable() {
            @Override
            public Renderer render() {

                if (optionMap == null) {
                    // for static options
                    Renderer renderer = Renderer.create();
                    final List<String> inputIdList = new LinkedList<>();
                    renderer.add(editSelector, new ElementSetter() {
                        @Override
                        public void set(Element elem) {
                            inputIdList.add(elem.id());
                        }
                    });
                    renderer.add(":root", new Renderable() {
                        @Override
                        public Renderer render() {
                            Renderer render = Renderer.create();
                            for (String id : inputIdList) {
                                render.add(SelectorUtil.attr(labelWrapperIndicatorAttr, id), LABEL_REF_ATTR, id);
                                render.add(SelectorUtil.attr("label", "for", id), LABEL_REF_ATTR, id);
                            }
                            return render;
                        }
                    });

                    if (duplicateSelector != null) {
                        renderer.add(duplicateSelector, new Renderable() {
                            @Override
                            public Renderer render() {
                                String duplicatorRef = IdGenerator.createId();
                                Renderer render = Renderer.create(":root", DUPLICATOR_REF_ID_ATTR, duplicatorRef);
                                render.add("input", DUPLICATOR_REF_ATTR, duplicatorRef);
                                String labelSelector;
                                if (labelWrapperIndicatorAttr == null) {
                                    labelSelector = SelectorUtil.tag("label");
                                } else {
                                    labelSelector = SelectorUtil.attr(labelWrapperIndicatorAttr);
                                }
                                render.add(labelSelector, DUPLICATOR_REF_ATTR, duplicatorRef);
                                return render;
                            }
                        });
                    }
                    return renderer;
                } else {
                    if (wrapperIdHolder.wrapperId == null && duplicateSelector == null) {
                        // for display mode?
                        return Renderer.create();
                    }
                    if (wrapperIdHolder.inputId == null) {
                        // target input element not found
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
                                duplicatorRef = IdGenerator.createId();
                            }

                            renderer.add(":root", DUPLICATOR_REF_ID_ATTR, duplicatorRef);

                            renderer.add(inputSelector, DUPLICATOR_REF_ATTR, duplicatorRef);
                            renderer.add(inputSelector, "id", newInputId);

                            // may be a wrapper container of label
                            renderer.add(wrapperIdHolder.labelSelector, LABEL_REF_ATTR, newInputId);
                            if (labelWrapperIndicatorAttr != null) {
                                renderer.add(wrapperIdHolder.labelSelector, labelWrapperIndicatorAttr, newInputId);
                            }
                            renderer.add(wrapperIdHolder.labelSelector, DUPLICATOR_REF_ATTR, duplicatorRef);

                            renderer.add("label", "for", newInputId);
                            renderer.add("label", row.getDisplayText());
                            return renderer;
                        }
                    });
                }
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

        PrepareRenderingDataUtil.storeDataToContextBySelector(editSelector, displaySelector, optionMap);

        return renderer;
    }

    @Override
    public Renderer postRender(String editSelector, String displaySelector) {

        Renderer render = Renderer.create();

        String[] clearAttrs = { LABEL_REF_ATTR, DUPLICATOR_REF_ATTR, DUPLICATOR_REF_ID_ATTR };
        for (String attr : clearAttrs) {
            render.add(SelectorUtil.attr(attr), attr, Clear);
        }
        return render.add(super.postRender(editSelector, displaySelector));
    }

}
