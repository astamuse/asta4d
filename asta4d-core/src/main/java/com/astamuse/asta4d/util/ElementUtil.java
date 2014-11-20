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

package com.astamuse.asta4d.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.astamuse.asta4d.extnode.GroupNode;

public class ElementUtil {

    public ElementUtil() {
    }

    public final static Element text(String text) {
        TextNode node = new TextNode(text, "");
        Element wrap = new GroupNode();
        wrap.appendChild(node);
        return wrap;
    }

    /**
     * parse given html source to a single Element
     * <p>
     * <b>ATTENTION</b>: this method will cause a potential XSS problem, so be sure that you have escaped the passed html string if
     * necessary.
     * 
     * @param html
     *            the html source
     * @return a Element object which contains the dom tree created from passed html source
     */
    public final static Element parseAsSingle(String html) {
        Element body = Jsoup.parseBodyFragment(html).body();
        List<Node> children = body.childNodes();
        return wrapElementsToSingleNode(children);
    }

    public final static Element wrapElementsToSingleNode(List<Node> elements) {
        Element groupNode = new GroupNode();
        List<Node> list = new ArrayList<Node>(elements);
        for (Node node : list) {
            node.remove();
            groupNode.appendChild(node);
        }
        return groupNode;
    }

    public final static void removeNodesBySelector(Element target, String selector, boolean pullupChildren) {
        Elements removeNodes = target.select(selector);
        Iterator<Element> it = removeNodes.iterator();
        Element rm;
        while (it.hasNext()) {
            rm = it.next();
            if (target == rm) {
                continue;
            }
            if (rm.ownerDocument() == null) {
                continue;
            }
            if (pullupChildren) {
                pullupChildren(rm);
            }
            rm.remove();
        }
    }

    public final static void pullupChildren(Element elem) {
        List<Node> childrenNodes = new ArrayList<>(elem.childNodes());
        for (Node node : childrenNodes) {
            node.remove();
            elem.before(node);
        }
    }
}
