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

    public final static Elements parse(String html) {
        return Jsoup.parseBodyFragment(html).select("body");
    }

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

    /**
     * there is a bug in jsoup, so we implement a safe empty by ourselves.
     * https://github.com/jhy/jsoup/issues/239
     * 
     * @param node
     */
    public final static void safeEmpty(Node node) {
        List<Node> children = new ArrayList<>(node.childNodes());
        for (Node child : children) {
            child.remove();
        }
    }

    public final static void appendNodes(Element parent, List<Node> children) {
        for (Node node : children) {
            parent.appendChild(node);
        }
    }

    public final static void removeNodesBySelector(Element target, String selector, boolean pullupChildren) {
        Elements removeNodes = target.select(selector);
        Iterator<Element> it = removeNodes.iterator();
        Element rm;
        while (it.hasNext()) {
            rm = it.next();
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
