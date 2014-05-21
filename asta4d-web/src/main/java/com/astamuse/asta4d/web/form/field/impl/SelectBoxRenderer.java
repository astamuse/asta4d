package com.astamuse.asta4d.web.form.field.impl;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.web.form.field.SimpleFormFieldValueRenderer;

public class SelectBoxRenderer extends SimpleFormFieldValueRenderer {

    private static final String valueMapStoreKey(String editSelector) {
        return editSelector + "#" + SelectBoxRenderer.class.getName();
    }

    public static final void storeValueMapToContext(String editTargetSelector, String displayTargetSelector, Map<String, String> valueMap) {
        Context context = Context.getCurrentThreadContext();

        String storeKey = valueMapStoreKey(editTargetSelector);
        context.setData(storeKey, valueMap);

        storeKey = valueMapStoreKey(displayTargetSelector);
        context.setData(storeKey, valueMap);
    }

    protected Map<String, String> retrieveStoredValueMapFromContext(String selector) {
        String storeKey = valueMapStoreKey(selector);
        Context context = Context.getCurrentThreadContext();
        return context.getData(storeKey);
    }

    @Override
    public Renderer renderForEdit(String nonNullString) {
        Renderer renderer = Renderer.create("option", "selected", Clear);

        String selector = "option[value=" + nonNullString + "]";
        renderer.add(selector, "selected", "");
        return renderer;
    }

    @Override
    public Renderer renderForDisplay(final String editTargetSelector, final String displayTargetSelector, final String nonNullString) {

        Renderer render = Renderer.create();

        Map<String, String> storedValueMap = retrieveStoredValueMapFromContext(editTargetSelector);

        // retrieve and create a value map here
        if (storedValueMap == null) {
            render.add(editTargetSelector, new ElementSetter() {
                @Override
                public void set(Element elem) {
                    Elements opts = elem.select("option");
                    String value, displayText;
                    Map<String, String> valueMap = new HashMap<>();
                    for (Element opt : opts) {
                        value = opt.attr("value");
                        displayText = opt.text();
                        valueMap.put(value, displayText);
                    }
                    storeValueMapToContext(editTargetSelector, displayTargetSelector, valueMap);
                }
            });
        }

        return render.add(super.renderForDisplay(editTargetSelector, displayTargetSelector, nonNullString));
    }

    private String retrieveDisplayString(String editTargetSelector, String nonNullString) {
        Map<String, String> storedValueMap = retrieveStoredValueMapFromContext(editTargetSelector);
        if (storedValueMap == null) {
            return nonNullString;
        }
        String value = storedValueMap.get(nonNullString);
        return value == null ? "" : value;
    }

    @Override
    protected Renderer renderToDisplayTarget(String displayTargetSelector, String nonNullString) {
        return super.renderToDisplayTarget(displayTargetSelector, retrieveDisplayString(displayTargetSelector, nonNullString));
    }

    @Override
    protected Renderer addAlternativeDom(String editTargetSelector, String nonNullString) {
        return super.addAlternativeDom(editTargetSelector, retrieveDisplayString(editTargetSelector, nonNullString));
    }

}
