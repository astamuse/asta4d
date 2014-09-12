package com.astamuse.asta4d.web.form.field;

import com.astamuse.asta4d.render.Renderer;

public abstract class SimpleFormFieldWithOptionValueRenderer extends SimpleFormFieldValueRenderer {

    protected String retrieveDisplayString(String selector, String nonNullString) {
        OptionValueMap storedOptionMap = AdditionalDataUtil.retrieveStoredDataFromContextBySelector(selector);
        if (storedOptionMap == null) {
            return nonNullString;
        }
        String value = storedOptionMap.getDisplayText(nonNullString);
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
