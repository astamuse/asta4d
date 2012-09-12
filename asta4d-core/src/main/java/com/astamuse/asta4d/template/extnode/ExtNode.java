package com.astamuse.asta4d.template.extnode;

import java.util.Iterator;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public class ExtNode extends Element {

    public ExtNode(String tag) {
        super(Tag.valueOf(tag), "");
    }

    public ExtNode(String tag, String cssClass) {
        this(tag);
        this.addClass(cssClass);
    }

    public void copyAttributes(Element src) {
        Attributes attrs = src.attributes();
        Iterator<Attribute> it = attrs.iterator();
        Attribute attr;
        while (it.hasNext()) {
            attr = it.next();
            this.attr(attr.getKey(), attr.getValue());
        }
    }
}
