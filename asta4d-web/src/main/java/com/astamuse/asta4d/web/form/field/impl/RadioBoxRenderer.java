package com.astamuse.asta4d.web.form.field.impl;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Element;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.render.ElementNotFoundHandler;
import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.Renderable;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.SelectorUtil;
import com.astamuse.asta4d.web.form.field.AdditionalDataUtil;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;
import com.astamuse.asta4d.web.form.field.SimpleFormFieldWithOptionValueRenderer;

public class RadioBoxRenderer extends SimpleFormFieldWithOptionValueRenderer {

    private static final String ToBeHiddenLaterFlagAttr = Configuration.getConfiguration().getTagNameSpace() + ":" +
            "ToBeHiddenLaterFlagAttr";

    @Override
    public Renderer renderForEdit(final String nonNullString) {
        Renderer renderer = Renderer.create("input", "checked", Clear);
        // we have to iterate the elements because the attr selector would not work for blank values.
        renderer.add("input", new ElementSetter() {
            @Override
            public void set(Element elem) {
                String val = elem.attr("value");
                if (nonNullString.equals(val)) {
                    elem.attr("checked", "");
                }
            }
        });
        return renderer;
    }

    protected Renderer retrieveAndCreateValueMap(final String editTargetSelector, final String displayTargetSelector) {
        Renderer render = Renderer.create();
        if (AdditionalDataUtil.retrieveStoredDataFromContextBySelector(editTargetSelector) == null) {

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
                                AdditionalDataUtil.storeDataToContextBySelector(editTargetSelector, displayTargetSelector,
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

    private Renderer superHideTarget(String targetSelector) {
        return super.hideTarget(targetSelector);
    }

    @Override
    protected Renderer hideTarget(final String targetSelector) {
        // hide the input element
        final List<String> duplicatorRefList = new LinkedList<>();
        final List<String> idList = new LinkedList<>();
        Renderer renderer = Renderer.create(targetSelector, new ElementSetter() {
            @Override
            public void set(Element elem) {
                String duplicatorRef = elem.attr(RadioBoxDataPrepareRenderer.DUPLICATOR_REF_ATTR);
                if (StringUtils.isNotEmpty(duplicatorRef)) {
                    duplicatorRefList.add(duplicatorRef);
                }
                idList.add(elem.id());
            }
        });
        return renderer.add(":root", new Renderable() {
            @Override
            public Renderer render() {
                Renderer render = Renderer.create();
                for (String ref : duplicatorRefList) {
                    render.add(SelectorUtil.attr(RadioBoxDataPrepareRenderer.DUPLICATOR_REF_ID_ATTR, ref), ToBeHiddenLaterFlagAttr, "");
                }
                for (String id : idList) {
                    render.add(SelectorUtil.attr(RadioBoxDataPrepareRenderer.LABEL_REF_ATTR, id), ToBeHiddenLaterFlagAttr, "");
                }
                for (String id : idList) {
                    render.add(SelectorUtil.attr("label", "for", id), ToBeHiddenLaterFlagAttr, "");
                }
                render.add(targetSelector, ToBeHiddenLaterFlagAttr, "");
                return render;
            }
        });
    }

    @Override
    public Renderer renderForDisplay(final String editTargetSelector, final String displayTargetSelector, final String nonNullString) {

        Renderer render = Renderer.create();

        // retrieve and create a value map here
        render.add(retrieveAndCreateValueMap(editTargetSelector, displayTargetSelector));

        render.add(super.renderForDisplay(editTargetSelector, displayTargetSelector, nonNullString));

        // delay to hide all
        render.add(":root", new Renderable() {
            @Override
            public Renderer render() {
                return superHideTarget(SelectorUtil.attr(ToBeHiddenLaterFlagAttr));
            }
        });

        // delay to remove the redundant attr
        render.add(":root", new Renderable() {
            @Override
            public Renderer render() {
                return Renderer.create(SelectorUtil.attr(ToBeHiddenLaterFlagAttr), ToBeHiddenLaterFlagAttr, Clear);
            }
        });
        return render;
    }

    @Override
    protected Renderer addAlternativeDom(final String editTargetSelector, final String nonNullString) {
        Renderer renderer = Renderer.create();

        // renderer.addDebugger("entry root");

        // renderer.addDebugger("entry root:edit target:", editTargetSelector);

        final List<String> matchedIdList = new LinkedList<>();
        final List<String> unMatchedIdList = new LinkedList<>();

        renderer.add(editTargetSelector, new ElementSetter() {
            @Override
            public void set(Element elem) {
                if (nonNullString.equals(elem.attr("value"))) {
                    matchedIdList.add(elem.id());
                } else {
                    unMatchedIdList.add(elem.id());
                }
            }
        });

        renderer.add(":root", new Renderable() {
            @Override
            public Renderer render() {

                System.err.println("editTargetSelector:" + editTargetSelector);
                System.err.println("matchedIdList" + matchedIdList);
                System.err.println("unMatchedIdList" + unMatchedIdList);

                Renderer renderer = Renderer.create();

                // renderer.addDebugger("before hide unmatch");
                // renderer.addDebugger("before add match");

                if (matchedIdList.isEmpty()) {
                    renderer.add(addAlternativeDomWhenMatchedLabelNotExists(editTargetSelector, nonNullString));
                } else {
                    // do nothing for remaining the existing label element
                    // but we still have to revive the possibly existing duplicate container
                    for (final String inputId : matchedIdList) {
                        final List<String> matchedDuplicatorRefList = new LinkedList<>();
                        final String labelRefSelector = SelectorUtil.attr(RadioBoxDataPrepareRenderer.LABEL_REF_ATTR, inputId);
                        final String labelDefaultSelector = SelectorUtil.attr(SelectorUtil.tag("label"), "for", inputId);
                        renderer.add(labelRefSelector, new ElementSetter() {
                            @Override
                            public void set(Element elem) {
                                String ref = elem.attr(RadioBoxDataPrepareRenderer.DUPLICATOR_REF_ATTR);
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
                                        String ref = elem.attr(RadioBoxDataPrepareRenderer.DUPLICATOR_REF_ATTR);
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
                                Renderer renderer = Renderer.create();
                                for (String ref : matchedDuplicatorRefList) {
                                    renderer.add(SelectorUtil.attr(RadioBoxDataPrepareRenderer.DUPLICATOR_REF_ID_ATTR, ref),
                                            ToBeHiddenLaterFlagAttr, Clear);
                                }
                                renderer.add(labelRefSelector, ToBeHiddenLaterFlagAttr, Clear);
                                renderer.add(labelDefaultSelector, ToBeHiddenLaterFlagAttr, Clear);
                                return renderer;
                            }
                        });
                    }
                }
                return renderer;
            }
        });

        return renderer;

    }

    private static class FlagHolder {
        boolean hasRendered = false;
    }

    private Renderer superAddAlternativeDom(String editTargetSelector, String nonNullString) {
        return super.addAlternativeDom(editTargetSelector, nonNullString);
    }

    private Renderer addAlternativeDomWhenMatchedLabelNotExists(final String editTargetSelector, final String nonNullString) {
        Renderer renderer = Renderer.create();
        final FlagHolder fh = new FlagHolder();
        return renderer.add(editTargetSelector, new Renderable() {
            @Override
            public Renderer render() {
                if (fh.hasRendered) {
                    return Renderer.create();
                } else {
                    fh.hasRendered = true;
                    return superAddAlternativeDom(editTargetSelector, nonNullString);
                }
            }
        });

    }
}
