package org.jsoupit.template.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoupit.template.extnode.GroupNode;

public class HtmlUtil {

    public HtmlUtil() {
    }

    public final static Elements parseHTML(String html) {
        return Jsoup.parseBodyFragment(html).select("body");
    }

    public final static Element parseHTMLAsSingleElement(String html) {
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
}
