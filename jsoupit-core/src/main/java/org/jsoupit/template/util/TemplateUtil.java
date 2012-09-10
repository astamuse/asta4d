package org.jsoupit.template.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoupit.Configuration;
import org.jsoupit.Context;
import org.jsoupit.template.Template;
import org.jsoupit.template.TemplateException;
import org.jsoupit.template.TemplateResolver;
import org.jsoupit.template.extnode.ExtNodeConstants;
import org.jsoupit.template.extnode.GroupNode;
import org.jsoupit.template.extnode.SnippetNode;

public class TemplateUtil {
    public final static void regulateElement(Element elem) {
        regulateSnippets(elem);
        regulateEmbed(elem);
    }

    private final static void regulateSnippets(Element elem) {

        // find nodes emebed with snippet attribute
        String snippetSelector = SelectorUtil.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_NAME);
        List<Element> embedSnippets = new ArrayList<>(elem.select(snippetSelector));
        // Element
        // Node parent;
        SnippetNode fakedSnippetNode;
        String render;
        for (Element element : embedSnippets) {
            fakedSnippetNode = new SnippetNode();
            // fakedSnippetNode
            element.after(fakedSnippetNode);
            element.remove();
            fakedSnippetNode.appendChild(element);

            render = element.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_NAME);
            element.removeAttr(ExtNodeConstants.SNIPPET_NODE_ATTR_NAME);
            fakedSnippetNode.copyAttributes(element);
            fakedSnippetNode.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_RENDER, render);
        }

        /*
         * set all the nodes without status attribute or with an illegal status 
         * value to ready 
         */

        // first, we regulate the snippets to legal status
        List<Element> snippetNodes = elem.select(ExtNodeConstants.SNIPPET_NODE_TAG_SELECTOR);
        String status;
        String id;
        for (Element sn : snippetNodes) {
            if (sn.hasAttr(ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS)) {
                status = sn.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS);
                switch (status) {
                case ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_READY:
                case ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_FINISHED:
                    // do nothing;
                    break;
                default:
                    sn.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS, ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_READY);
                }
            } else {
                sn.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS, ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_READY);
            }
            if (!sn.hasAttr(ExtNodeConstants.SNIPPET_NODE_ATTR_REFID)) {
                id = "sn-" + IdGenerator.createId();
                sn.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_REFID, id);
            }
        }

        // then let us check the nested relation for nodes without block attr
        snippetSelector = SelectorUtil.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_BLOCK);
        snippetSelector = SelectorUtil.not(ExtNodeConstants.SNIPPET_NODE_TAG_SELECTOR, snippetSelector);
        snippetNodes = elem.select(snippetSelector);
        setBlockingParentSnippetId(snippetNodes);
    }

    private final static void regulateEmbed(Element elem) {
        // check nodes without block attr for blocking parent snippets
        String selector = SelectorUtil.attr(ExtNodeConstants.EMBED_NODE_ATTR_BLOCK);
        selector = SelectorUtil.not(ExtNodeConstants.EMBED_NODE_TAG_SELECTOR, selector);
        List<Element> embedElemes = elem.select(selector);
        setBlockingParentSnippetId(embedElemes);
    }

    private final static void setBlockingParentSnippetId(List<Element> elems) {
        Element searchElem;
        String blockingParentId;
        for (Element elem : elems) {
            searchElem = elem.parent();
            blockingParentId = "";
            while (searchElem != null) {
                if (searchElem.tagName().equals(ExtNodeConstants.SNIPPET_NODE_TAG)) {
                    blockingParentId = searchElem.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_REFID);
                    break;
                } else {
                    searchElem = searchElem.parent();
                }
            }
            elem.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_BLOCK, blockingParentId);
            // TODO should we replace the blocked element to a dummy place
            // holder element to avoid being rendered by parent snippets
        }
    }

    public final static Element getEmbedNodeContent(Element elem) throws TemplateException {
        String target;
        Configuration conf = Context.getCurrentThreadContext().getConfiguration();
        TemplateResolver templateResolver = conf.getTemplateResolver();
        target = elem.attr(ExtNodeConstants.EMBED_NODE_ATTR_TARGET);
        if (target == null || target.isEmpty()) {
            String message = "Target not defined[" + elem.toString() + "]";
            throw new TemplateException(message);
        }
        Template embedTarget = templateResolver.findTemplate(target);
        if (embedTarget == null) {
            String message = "Target of emebed node not found[" + elem.toString() + "]";
            throw new TemplateException(message);
        }

        // copy all the attrs to the wrapping group node
        Element groupNode = new GroupNode();
        Iterator<Attribute> attrs = elem.attributes().iterator();
        Attribute attr;
        while (attrs.hasNext()) {
            attr = attrs.next();
            groupNode.attr(attr.getKey(), attr.getValue());
        }

        Element body = embedTarget.getDocumentClone().body();
        List<Node> children = new ArrayList<Node>(body.childNodes());
        for (Node node : children) {
            node.remove();
            groupNode.appendChild(node);
        }
        return groupNode;
    }
}
