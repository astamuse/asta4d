package com.astamuse.asta4d.web.form.field;

import java.io.Serializable;

public class OptionValuePair implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String value;

    private String displayText;

    public OptionValuePair(String value, String displayText) {
        super();
        this.value = value;
        this.displayText = displayText;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayText() {
        return displayText;
    }

}
