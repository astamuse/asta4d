package com.astamuse.asta4d.web.form.field.impl;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Element;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.extnode.GroupNode;
import com.astamuse.asta4d.render.ElementNotFoundHandler;
import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.Renderable;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.render.transformer.ElementTransformer;
import com.astamuse.asta4d.util.ElementUtil;
import com.astamuse.asta4d.util.SelectorUtil;
import com.astamuse.asta4d.util.collection.RowRenderer;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;
import com.astamuse.asta4d.web.form.field.PrepareRenderingDataUtil;
import com.astamuse.asta4d.web.form.field.SimpleFormFieldWithOptionValueRenderer;

public class AbstractRadioAndCheckboxRenderer extends SimpleFormFieldWithOptionValueRenderer {

    private static final String ToBeHiddenLaterFlagAttr = Configuration.getConfiguration().getTagNameSpace() + ":" +
            "ToBeHiddenLaterFlagAttr";

    @Override
    public Renderer renderForEdit(String editTargetSelector, Object value) {
        final List<String> valueList = convertValueToList(value);
        Renderer renderer = Renderer.create("input", "checked", Clear);
        // we have to iterate the elements because the attr selector would not work for blank values.
        renderer.add("input", new ElementSetter() {
            @Override
            public void set(Element elem) {
                String val = elem.attr("value");
                if (valueList.contains(val)) {
                    elem.attr("checked", "");
                }
            }
        });
        return Renderer.create(editTargetSelector, renderer);
    }

    protected Renderer retrieveAndCreateValueMap(final String editTargetSelector, final String displayTargetSelector) {
        Renderer render = Renderer.create();
        if (PrepareRenderingDataUtil.retrieveStoredDataFromContextBySelector(editTargetSelector) == null) {

            final List<Pair<String, String>> inputList = new LinkedList<>();

            final List<OptionValuePair> optionList = new LinkedList<>();

            render.add(editTargetSelector, new ElementSetter() {
                @Override
                public void set(Element elem) {
                    inputList.add(Pair.of(elem.id(), elem.attr("value")));
                }
            });

            render.add(":root", new Renderable() {
                @Override
                public Renderer render() {
                    Renderer render = Renderer.create();
                    for (Pair<String, String> input : inputList) {
                        String id = input.getLeft();
                        final String value = input.getRight();
                        render.add(SelectorUtil.attr("for", id), Renderer.create("label", new ElementSetter() {
                            @Override
                            public void set(Element elem) {
                                optionList.add(new OptionValuePair(value, elem.text()));
                            }
                        }));
                        render.add(":root", new Renderable() {
                            @Override
                            public Renderer render() {
                                PrepareRenderingDataUtil.storeDataToContextBySelector(editTargetSelector, displayTargetSelector,
                                        new OptionValueMap(optionList));
                                return Renderer.create();
                            }
                        });
                    }// end for loop
                    return render;
                }
            });
        }
        return render;
    }

    protected Renderer setDelayedHiddenFlag(final String targetSelector) {
        // hide the input element
        final List<String> duplicatorRefList = new LinkedList<>();
        final List<String> idList = new LinkedList<>();
        Renderer renderer = Renderer.create(targetSelector, new ElementSetter() {
            @Override
            public void set(Element elem) {
                String duplicatorRef = elem.attr(RadioPrepareRenderer.DUPLICATOR_REF_ATTR);
                if (StringUtils.isNotEmpty(duplicatorRef)) {
                    duplicatorRefList.add(duplicatorRef);
                }
                idList.add(elem.id());
            }
        });
        return renderer.add(":root", new Renderable() {
            @Override
            public Renderer render() {
                Renderer render = Renderer.create().disableMissingSelectorWarning();

                for (String ref : duplicatorRefList) {
                    render.add(SelectorUtil.attr(RadioPrepareRenderer.DUPLICATOR_REF_ID_ATTR, ref), ToBeHiddenLaterFlagAttr, "");
                }
                for (String id : idList) {
                    render.add(SelectorUtil.attr(RadioPrepareRenderer.LABEL_REF_ATTR, id), ToBeHiddenLaterFlagAttr, "");
                }
                for (String id : idList) {
                    render.add(SelectorUtil.attr("label", "for", id), ToBeHiddenLaterFlagAttr, "");
                }
                render.add(targetSelector, ToBeHiddenLaterFlagAttr, "");
                // render.addDebugger("after set hidden flag");
                return render.enableMissingSelectorWarning();
            }
        });
    }

    @Override
    public Renderer renderForDisplay(final String editTargetSelector, final String displayTargetSelector, final Object value) {

        Renderer render = Renderer.create().disableMissingSelectorWarning();

        // retrieve and create a value map here
        render.add(retrieveAndCreateValueMap(editTargetSelector, displayTargetSelector));

        // render.add(super.renderForDisplay(editTargetSelector, displayTargetSelector, nonNullString));

        // hide the edit element
        render.add(setDelayedHiddenFlag(editTargetSelector));

        final List<String> valueList = convertValueToList(value);

        // render the shown value to target element by displayTargetSelector
        render.add(displayTargetSelector, new Renderable() {

            @Override
            public Renderer render() {
                return Renderer.create(displayTargetSelector, valueList, new RowRenderer<String>() {
                    @Override
                    public Renderer convert(int rowIndex, String v) {
                        return renderToDisplayTarget(displayTargetSelector,
                                retrieveDisplayStringFromStoredOptionValueMap(displayTargetSelector, v));
                    }
                });
            }
        });

        // if the element by displayTargetSelector does not exists, simply add a span to show the value.
        // since ElementNotFoundHandler has been delayed, so the Renderable is not necessary
        render.add(new ElementNotFoundHandler(displayTargetSelector) {
            @Override
            public Renderer alternativeRenderer() {
                return addAlternativeDom(editTargetSelector, valueList);
            }
        });

        // delay to hide all
        render.add(":root", new Renderable() {
            @Override
            public Renderer render() {
                return hideTarget(SelectorUtil.attr(ToBeHiddenLaterFlagAttr));
            }
        });

        // delay to remove the redundant attr
        render.add(":root", new Renderable() {
            @Override
            public Renderer render() {
                Renderer render = Renderer.create().disableMissingSelectorWarning();
                return render.add(SelectorUtil.attr(ToBeHiddenLaterFlagAttr), ToBeHiddenLaterFlagAttr, Clear)
                        .enableMissingSelectorWarning();
            }
        });
        return render.enableMissingSelectorWarning();
    }

    protected Renderer addAlternativeDom(final String editTargetSelector, final List<String> valueList) {
        Renderer renderer = Renderer.create();

        // renderer.addDebugger("entry root");

        // renderer.addDebugger("entry root:edit target:", editTargetSelector);

        final List<String> matchedIdList = new LinkedList<>();
        final List<String> unMatchedIdList = new LinkedList<>();

        renderer.add(editTargetSelector, new ElementSetter() {
            @Override
            public void set(Element elem) {
                if (valueList.contains((elem.attr("value")))) {
                    matchedIdList.add(elem.id());
                } else {
                    unMatchedIdList.add(elem.id());
                }
            }
        });

        renderer.add(":root", new Renderable() {
            @Override
            public Renderer render() {
                Renderer renderer = Renderer.create().disableMissingSelectorWarning();

                // renderer.addDebugger("before hide unmatch");
                // renderer.addDebugger("before add match");

                if (matchedIdList.isEmpty()) {
                    renderer.add(addDefaultAlternativeDom(editTargetSelector, valueList));
                } else {
                    // do nothing for remaining the existing label element
                    // but we still have to revive the possibly existing duplicate container
                    for (final String inputId : matchedIdList) {
                        final List<String> matchedDuplicatorRefList = new LinkedList<>();
                        final String labelRefSelector = SelectorUtil.attr(RadioPrepareRenderer.LABEL_REF_ATTR, inputId);
                        final String labelDefaultSelector = SelectorUtil.attr(SelectorUtil.tag("label"), "for", inputId);
                        renderer.add(labelRefSelector, new ElementSetter() {
                            @Override
                            public void set(Element elem) {
                                String ref = elem.attr(RadioPrepareRenderer.DUPLICATOR_REF_ATTR);
                                if (StringUtils.isNotEmpty(ref)) {
                                    matchedDuplicatorRefList.add(ref);
                                }
                            }
                        });
                        renderer.add(new ElementNotFoundHandler(labelRefSelector) {
                            @Override
                            public Renderer alternativeRenderer() {
                                return Renderer.create(labelDefaultSelector, new ElementSetter() {
                                    @Override
                                    public void set(Element elem) {
                                        String ref = elem.attr(RadioPrepareRenderer.DUPLICATOR_REF_ATTR);
                                        if (StringUtils.isNotEmpty(ref)) {
                                            matchedDuplicatorRefList.add(ref);
                                        }
                                    }// end set
                                });
                            }// end alternativeRenderer
                        });// end ElementNotFoundHandler
                        renderer.add(":root", new Renderable() {
                            @Override
                            public Renderer render() {
                                Renderer renderer = Renderer.create().disableMissingSelectorWarning();
                                for (String ref : matchedDuplicatorRefList) {
                                    renderer.add(SelectorUtil.attr(RadioPrepareRenderer.DUPLICATOR_REF_ID_ATTR, ref),
                                            ToBeHiddenLaterFlagAttr, Clear);
                                }
                                renderer.add(labelRefSelector, ToBeHiddenLaterFlagAttr, Clear);
                                renderer.add(labelDefaultSelector, ToBeHiddenLaterFlagAttr, Clear);
                                return renderer.enableMissingSelectorWarning();
                            }
                        });
                    }
                }
                return renderer.enableMissingSelectorWarning();
            }
        });

        return renderer;

    }

    protected Renderer addDefaultAlternativeDom(final String editTargetSelector, final List<String> valueList) {
        final List<String> duplicatorRefList = new LinkedList<>();
        final List<String> idList = new LinkedList<>();
        Renderer renderer = Renderer.create(editTargetSelector, new ElementSetter() {
            @Override
            public void set(Element elem) {
                String duplicatorRef = elem.attr(RadioPrepareRenderer.DUPLICATOR_REF_ATTR);
                if (StringUtils.isNotEmpty(duplicatorRef)) {
                    duplicatorRefList.add(duplicatorRef);
                }
                idList.add(elem.id());
            }
        });
        renderer.add(":root", new Renderable() {
            @Override
            public Renderer render() {
                String attachTargetSelector;
                if (duplicatorRefList.size() > 0) {
                    attachTargetSelector = SelectorUtil.attr(RadioPrepareRenderer.DUPLICATOR_REF_ID_ATTR,
                            duplicatorRefList.get(duplicatorRefList.size() - 1));
                } else {
                    attachTargetSelector = SelectorUtil.id(idList.get(idList.size() - 1));
                }
                return new Renderer(attachTargetSelector, new ElementTransformer(null) {
                    @Override
                    public Element invoke(Element elem) {
                        GroupNode group = new GroupNode();

                        Element editClone = ElementUtil.safeClone(elem);
                        group.appendChild(editClone);

                        for (String v : valueList) {
                            String nonNullString = retrieveDisplayStringFromStoredOptionValueMap(editTargetSelector, v);
                            group.appendChild(createAlternativeDisplayElement(nonNullString));
                        }
                        return group;
                    }// invoke
                });// new renderer
            }// render()
        });// renderable

        return renderer;
    }

}
