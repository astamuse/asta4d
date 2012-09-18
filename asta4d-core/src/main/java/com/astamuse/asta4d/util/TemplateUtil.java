package com.astamuse.asta4d.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.extnode.ExtNodeConstants;
import com.astamuse.asta4d.extnode.GroupNode;
import com.astamuse.asta4d.extnode.SnippetNode;
import com.astamuse.asta4d.template.Template;
import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.template.TemplateResolver;

public class TemplateUtil {

    private final static Logger logger = LoggerFactory.getLogger(TemplateUtil.class);

    public final static void regulateElement(Element elem) {
        regulateSnippets(elem);
        regulateEmbed(elem);
    }

    private final static void regulateSnippets(Element elem) {

        // find nodes emebed with snippet attribute
        String snippetSelector = SelectorUtil.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_RENDER);
        snippetSelector = SelectorUtil.not(snippetSelector, ExtNodeConstants.SNIPPET_NODE_TAG_SELECTOR);

        List<Element> embedSnippets = new ArrayList<>(elem.select(snippetSelector));
        // Element
        // Node parent;
        SnippetNode fakedSnippetNode;
        String render;
        for (Element element : embedSnippets) {

            render = element.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_RENDER);
            fakedSnippetNode = new SnippetNode(render);
            fakedSnippetNode.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE, ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE_FAKE);

            // move the original node under the faked node
            element.after(fakedSnippetNode);
            element.remove();
            fakedSnippetNode.appendChild(element);
            element.removeAttr(ExtNodeConstants.SNIPPET_NODE_ATTR_RENDER);
        }

        /*
         * set all the nodes without status attribute or with an illegal status 
         * value to ready 
         */

        // first, we regulate the snippets to legal form
        List<Element> snippetNodes = elem.select(ExtNodeConstants.SNIPPET_NODE_TAG_SELECTOR);
        String status;
        String id;
        for (Element sn : snippetNodes) {
            // regulate status
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
            // regulate id
            if (!sn.hasAttr(ExtNodeConstants.SNIPPET_NODE_ATTR_REFID)) {
                id = "sn-" + IdGenerator.createId();
                sn.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_REFID, id);
            }
            // regulate type
            if (!sn.hasAttr(ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE)) {
                sn.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE, ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE_USERDEFINE);
            }
            switch (sn.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE)) {
            case ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE_FAKE:
            case ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE_USERDEFINE:
                // do nothing;
                break;
            default:
                // we do not allow snippet node has user customized type
                // attribute
                sn.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE, ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE_USERDEFINE);
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

        Document embedDoc = embedTarget.getDocumentClone();
        /*
                Elements children = embedDoc.body().children();
                Element wrappingNode = ElementUtil.wrapElementsToSingleNode(children);
        */
        Element wrappingNode = new GroupNode();
        // retrieve all the blocks that misincluded into head
        Element head = embedDoc.head();
        Elements headChildren = head.children();
        List<Node> tempList = new ArrayList<>();
        String tagName;
        for (Element child : headChildren) {
            if (StringUtil.in(child.tagName(), "script", "link", ExtNodeConstants.BLOCK_NODE_TAG)) {
                child.remove();
                wrappingNode.appendChild(child);
            }
        }

        Element body = embedDoc.body();
        Elements bodyChildren = body.children();
        ElementUtil.appendNodes(wrappingNode, new ArrayList<Node>(bodyChildren));

        // copy all the attrs to the wrapping group node
        Iterator<Attribute> attrs = elem.attributes().iterator();
        Attribute attr;
        while (attrs.hasNext()) {
            attr = attrs.next();
            wrappingNode.attr(attr.getKey(), attr.getValue());
        }

        return wrappingNode;
    }

    public final static void mergeBlock(Document doc, Element content) {
        Iterator<Element> blockIterator = content.select(ExtNodeConstants.BLOCK_NODE_TAG_SELECTOR).iterator();
        Element block, targetBlock;
        String blockTarget, blockType;
        List<Node> childNodes;
        while (blockIterator.hasNext()) {
            block = blockIterator.next();
            if (block.hasAttr(ExtNodeConstants.BLOCK_NODE_ATTR_OVERRIDE)) {
                blockType = ExtNodeConstants.BLOCK_NODE_ATTR_OVERRIDE;
            } else if (block.hasAttr(ExtNodeConstants.BLOCK_NODE_ATTR_APPEND)) {
                blockType = ExtNodeConstants.BLOCK_NODE_ATTR_APPEND;
            } else if (block.hasAttr(ExtNodeConstants.BLOCK_NODE_ATTR_INSERT)) {
                blockType = ExtNodeConstants.BLOCK_NODE_ATTR_INSERT;
            } else {
                // TODO I want a approach to logging out template file path here
                logger.warn("The block does not declare its action correctlly.[{}]", block.toString());
                continue;
            }

            blockTarget = block.attr(blockType);
            if (blockTarget == null || blockTarget.isEmpty()) {
                // TODO I want a approach to logging out template file path here
                logger.warn("The block does not declare its target action correctlly.[{}]", block.toString());
                continue;
            }
            targetBlock = doc.select(SelectorUtil.id(ExtNodeConstants.BLOCK_NODE_TAG_SELECTOR, blockTarget)).first();
            if (targetBlock == null) {
                // TODO I want a approach to logging out template file path here
                logger.warn("The block declares a not existed target block.[{}]", block.toString());
                continue;
            }
            childNodes = new ArrayList<>(block.childNodes());
            ElementUtil.safeEmpty(block);
            switch (blockType) {
            case ExtNodeConstants.BLOCK_NODE_ATTR_OVERRIDE:
                targetBlock.empty();
                ElementUtil.appendNodes(targetBlock, childNodes);
                break;
            case ExtNodeConstants.BLOCK_NODE_ATTR_APPEND:
                ElementUtil.appendNodes(targetBlock, childNodes);
                break;
            case ExtNodeConstants.BLOCK_NODE_ATTR_INSERT:
                List<Node> originNodes = new ArrayList<>(targetBlock.childNodes());
                ElementUtil.safeEmpty(targetBlock);
                ElementUtil.appendNodes(targetBlock, childNodes);
                ElementUtil.appendNodes(targetBlock, originNodes);
                break;
            }
            block.remove();
        }
    }
}
