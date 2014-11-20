package com.astamuse.asta4d.web.form.field.impl;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public class SelectMultipleRenderer extends AbstractSelectRenderer {
    protected Element createAlternativeDisplayElement(String nonNullString) {
        Element span = new Element(Tag.valueOf("div"), "");
        span.text(nonNullString);
        return span;
    }
}
