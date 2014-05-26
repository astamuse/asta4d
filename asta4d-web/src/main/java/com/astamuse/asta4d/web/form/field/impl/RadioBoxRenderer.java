package com.astamuse.asta4d.web.form.field.impl;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Element;

import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.Renderable;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.SelectorUtil;
import com.astamuse.asta4d.web.form.field.AdditionalDataUtil;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;
import com.astamuse.asta4d.web.form.field.SimpleFormFieldWithOptionValueRenderer;

public class RadioBoxRenderer extends SimpleFormFieldWithOptionValueRenderer {

    @Override
    public Renderer renderForEdit(String nonNullString) {
        Renderer renderer = Renderer.create("option", "selected", Clear);
        if (!nonNullString.isEmpty()) {
            renderer.add(SelectorUtil.attr("value", nonNullString), "checked", "");
        }
        return renderer;
    }

    @Override
    public Renderer renderForDisplay(final String editTargetSelector, final String displayTargetSelector, final String nonNullString) {

        Renderer render = Renderer.create();

        // retrieve and create a value map here
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
                        render.add(SelectorUtil.attr(RadioBoxAdditionalRenderer.LABEL_REF_ATTR, id),
                                Renderer.create("label", new ElementSetter() {
                                    @Override
                                    public void set(Element elem) {
                                        optionList.add(new OptionValuePair(value, elem.text()));
                                    }
                                }));
                    }// end for loop
                    return render;
                }
            });

            AdditionalDataUtil.storeDataToContextBySelector(editTargetSelector, displayTargetSelector, new OptionValueMap(optionList));
        }

        return render.add(super.renderForDisplay(editTargetSelector, displayTargetSelector, nonNullString));
    }

    @Override
    protected Renderer addAlternativeDom(final String editTargetSelector, final String nonNullString) {
        Renderer renderer = Renderer.create();

        final String matchSelector = SelectorUtil.attr("value", nonNullString);
        final String unMatchSelector = SelectorUtil.not(matchSelector);

        final List<String> matchedIdList = new LinkedList<>();
        final List<String> unMatchedIdList = new LinkedList<>();

        renderer.add(editTargetSelector, new Renderable() {
            @Override
            public Renderer render() {
                Renderer renderer = Renderer.create(matchSelector, new ElementSetter() {
                    @Override
                    public void set(Element elem) {
                        matchedIdList.add(elem.id());
                    }
                });
                renderer.add(unMatchSelector, new ElementSetter() {
                    @Override
                    public void set(Element elem) {
                        unMatchedIdList.add(elem.id());
                    }
                });
                return renderer;
            }
        });

        renderer.add(":root", new Renderable() {
            @Override
            public Renderer render() {
                Renderer renderer = Renderer.create();
                for (String inputId : unMatchedIdList) {
                    renderer.add(hideTarget(SelectorUtil.attr(RadioBoxAdditionalRenderer.LABEL_REF_ATTR, inputId)));
                }
                if (matchedIdList.isEmpty()) {
                    renderer.add(superAddAlternativeDom(editTargetSelector, nonNullString));
                } else {
                    // do nothing for remaining the existing label element
                }
                return renderer;
            }
        });

        return renderer;

    }

    private Renderer superAddAlternativeDom(String editTargetSelector, String nonNullString) {
        return super.addAlternativeDom(editTargetSelector, retrieveDisplayString(editTargetSelector, nonNullString));
    }
}
