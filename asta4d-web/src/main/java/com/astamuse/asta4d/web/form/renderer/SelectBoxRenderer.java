package com.astamuse.asta4d.web.form.renderer;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.Renderer;

public class SelectBoxRenderer extends SimpleFormFieldValueRenderer {

    private static final String VALUE_MAP_KEY = "VALUE_MAP_KEY#" + SelectBoxRenderer.class.getName();

    @Override
    public Renderer renderForEdit(String nonNullString) {
        Renderer renderer = Renderer.create("option", "selected", Clear);

        String selector = "option[value=" + nonNullString + "]";
        renderer.add(selector, "selected", "");
        return renderer;
    }

    @Override
    public Renderer renderForDisplay(final String editTargetSelector, final String displayTargetSelector, final String nonNullString) {
        final Map<String, String> valueMap = new HashMap<>();
        Context.getCurrentThreadContext().setData(VALUE_MAP_KEY, valueMap);

        // retrieve and create a value map here
        Renderer render = Renderer.create(editTargetSelector, new ElementSetter() {
            @Override
            public void set(Element elem) {
                Elements opts = elem.select("option");
                String value, displayText;
                for (Element opt : opts) {
                    value = opt.attr("value");
                    displayText = opt.text();
                    valueMap.put(value, displayText);
                }
            }
        });

        return render.add(super.renderForDisplay(editTargetSelector, displayTargetSelector, nonNullString));
    }

    private String retrieveDisplayString(String nonNullString) {
        Map<String, String> valueMap = Context.getCurrentThreadContext().getData(VALUE_MAP_KEY);
        if (valueMap == null) {
            return nonNullString;
        }
        String value = valueMap.get(nonNullString);
        return value == null ? "" : value;
    }

    @Override
    protected Renderer renderToDisplayTarget(String displayTargetSelector, String nonNullString) {
        return super.renderToDisplayTarget(displayTargetSelector, retrieveDisplayString(nonNullString));
    }

    @Override
    protected Renderer addAlternativeDom(String editTargetSelector, String nonNullString) {
        return super.addAlternativeDom(editTargetSelector, retrieveDisplayString(nonNullString));
    }

}
