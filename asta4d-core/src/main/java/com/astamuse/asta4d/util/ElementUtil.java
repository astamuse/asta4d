package com.astamuse.asta4d.util;

import java.util.ArrayList;
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
        Elements elems = Jsoup.parseBodyFragment(html).body().children();
        if (elems.size() == 1) {
            return elems.first();
        } else {
            return wrapElementsToSingleNode(elems);
        }
    }

    public final static Element wrapElementsToSingleNode(Elements elements) {
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
}
