package com.astamuse.asta4d.web.form.field.impl;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.web.form.field.AdditionalDataUtil;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;
import com.astamuse.asta4d.web.form.field.SimpleFormFieldWithOptionValueRenderer;

public class SelectBoxRenderer extends SimpleFormFieldWithOptionValueRenderer {

    @Override
    public Renderer renderForEdit(final String nonNullString) {
        Renderer renderer = Renderer.create("option", "selected", Clear);
        String selector = "option";
        // we have to iterate the elements because the attr selector would not work for blank values.
        renderer.add(selector, new ElementSetter() {
            @Override
            public void set(Element elem) {
                String val = elem.attr("value");
                if (nonNullString.equals(val)) {
                    elem.attr("selected", "");
                }
            }
        });
        return renderer;
    }

    @Override
    public Renderer renderForDisplay(final String editTargetSelector, final String displayTargetSelector, final String nonNullString) {

        Renderer render = Renderer.create();

        // retrieve and create a value map here
        if (AdditionalDataUtil.retrieveStoredDataFromContextBySelector(editTargetSelector) == null) {

            render.add(editTargetSelector, new ElementSetter() {
                @Override
                public void set(Element elem) {
                    final List<OptionValuePair> optionList = new LinkedList<>();
                    Elements opts = elem.select("option");
                    String value, displayText;
                    for (Element opt : opts) {
                        value = opt.attr("value");
                        displayText = opt.text();
                        optionList.add(new OptionValuePair(value, displayText));
                    }
                    AdditionalDataUtil.storeDataToContextBySelector(editTargetSelector, displayTargetSelector, new OptionValueMap(
                            optionList));
                }
            });
        }

        return render.add(super.renderForDisplay(editTargetSelector, displayTargetSelector, nonNullString));
    }
}
