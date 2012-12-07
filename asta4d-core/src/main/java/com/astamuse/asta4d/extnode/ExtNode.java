/*
 * Copyright 2012 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.astamuse.asta4d.extnode;

import java.util.Iterator;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

/**
 * A node used for extending purpose.
 * 
 * @author e-ryu
 * 
 */
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
