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

package com.astamuse.asta4d.template;

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
import com.astamuse.asta4d.extnode.ExtNodeConstants;
import com.astamuse.asta4d.extnode.GroupNode;
import com.astamuse.asta4d.extnode.SnippetNode;
import com.astamuse.asta4d.util.ElementUtil;
import com.astamuse.asta4d.util.IdGenerator;
import com.astamuse.asta4d.util.SelectorUtil;

public class TemplateUtil {

    private final static Logger logger = LoggerFactory.getLogger(TemplateUtil.class);

    public final static void regulateElement(Document doc) throws TemplateException, TemplateNotFoundException {
        // disabled. see {@link #loadStaticEmebed}
        // load static embed at first
        // loadStaticEmebed(doc);
        regulateSnippets(doc);
        regulateEmbed(doc);
    }

    private final static String createSnippetRef() {
        return "sn-" + IdGenerator.createId();
    }

    private final static void regulateSnippets(Document doc) {

        // find nodes emebed with snippet attribute
        String snippetSelector = SelectorUtil.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_RENDER_WITH_NS);
        snippetSelector = SelectorUtil.not(snippetSelector, ExtNodeConstants.SNIPPET_NODE_TAG_SELECTOR);

        List<Element> embedSnippets = new ArrayList<>(doc.select(snippetSelector));
        // Element
        // Node parent;
        SnippetNode fakedSnippetNode;
        String render;
        for (Element element : embedSnippets) {

            render = element.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_RENDER_WITH_NS);
            fakedSnippetNode = new SnippetNode(render);
            fakedSnippetNode.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE, ExtNodeConstants.SNIPPET_NODE_ATTR_TYPE_FAKE);

            // move the original node under the faked node
            element.after(fakedSnippetNode);
            element.remove();
            fakedSnippetNode.appendChild(element);
            element.removeAttr(ExtNodeConstants.SNIPPET_NODE_ATTR_RENDER_WITH_NS);

            // set parallel type
            if (element.hasAttr(ExtNodeConstants.SNIPPET_NODE_ATTR_PARALLEL_WITH_NS)) {
                fakedSnippetNode.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_PARALLEL, "");
                element.removeAttr(ExtNodeConstants.SNIPPET_NODE_ATTR_PARALLEL_WITH_NS);
            }
        }

        /*
         * set all the nodes without status attribute or with an illegal status 
         * value to ready 
         */

        // first, we regulate the snippets to legal form
        List<Element> snippetNodes = doc.select(ExtNodeConstants.SNIPPET_NODE_TAG_SELECTOR);
        String status;
        for (Element sn : snippetNodes) {
            // regulate status
            if (sn.hasAttr(ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS)) {
                status = sn.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS);
                switch (status) {
                case ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_READY:
                case ExtNodeConstants.SNIPPET_NODE_ATTR_STATUS_WAITING:
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
            if (!sn.hasAttr(ExtNodeConstants.ATTR_SNIPPET_REF)) {
                sn.attr(ExtNodeConstants.ATTR_SNIPPET_REF, createSnippetRef());
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
        snippetNodes = doc.select(snippetSelector);
        setBlockingParentSnippetId(snippetNodes);
    }

    private final static void regulateEmbed(Document doc) throws TemplateException, TemplateNotFoundException {
        // check nodes without block attr for blocking parent snippets
        String selector = SelectorUtil.attr(ExtNodeConstants.EMBED_NODE_ATTR_BLOCK);
        selector = SelectorUtil.not(ExtNodeConstants.EMBED_NODE_TAG_SELECTOR, selector);
        List<Element> embedElemes = doc.select(selector);
        setBlockingParentSnippetId(embedElemes);
    }

    /**
     * Disabled static embed at 2014.09.26.
     * 
     * Developers would like to use different snippets to render a same static embed file as following: <code>
     *   <afd:snippet render="SomeSnippet">
     *      <afd:embed target="/someEmbed.html"/>
     *   </afd:snippet>
     * </code>
     * 
     * Which confuses rendering logic and makes bad source smell, thus we decide to disable this feature.
     * 
     * @param doc
     * @throws TemplateException
     * @throws TemplateNotFoundException
     */
    @Deprecated
    private final static void loadStaticEmebed(Document doc) throws TemplateException, TemplateNotFoundException {

        String selector = SelectorUtil.attr(SelectorUtil.tag(ExtNodeConstants.EMBED_NODE_TAG_SELECTOR),
                ExtNodeConstants.EMBED_NODE_ATTR_STATIC, null);

        int embedNodeListCount;
        do {
            List<Element> embedNodeList = doc.select(selector);
            embedNodeListCount = embedNodeList.size();
            Iterator<Element> embedNodeIterator = embedNodeList.iterator();
            Element embed;
            Element embedContent;
            while (embedNodeIterator.hasNext()) {
                embed = embedNodeIterator.next();
                embedContent = getEmbedNodeContent(embed);
                mergeBlock(doc, embedContent);
                embed.before(embedContent);
                embed.remove();
            }
        } while (embedNodeListCount > 0);

    }

    private final static void setBlockingParentSnippetId(List<Element> elems) {
        Element searchElem;
        String blockingParentId;
        for (Element elem : elems) {
            searchElem = elem.parent();
            blockingParentId = "";
            while (searchElem != null) {
                if (searchElem.tagName().equals(ExtNodeConstants.SNIPPET_NODE_TAG)) {
                    blockingParentId = searchElem.attr(ExtNodeConstants.ATTR_SNIPPET_REF);
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

    private final static void resetSnippetRefs(Element elem) {
        String snippetRefSelector = SelectorUtil.attr(ExtNodeConstants.ATTR_SNIPPET_REF);
        List<Element> snippets = new ArrayList<>(elem.select(snippetRefSelector));
        String oldRef, newRef;
        String blockedSnippetSelector;
        List<Element> blockedSnippets;
        for (Element element : snippets) {
            oldRef = element.attr(ExtNodeConstants.ATTR_SNIPPET_REF);
            newRef = createSnippetRef();

            // find blocked snippet
            blockedSnippetSelector = SelectorUtil.attr(ExtNodeConstants.SNIPPET_NODE_TAG_SELECTOR,
                    ExtNodeConstants.SNIPPET_NODE_ATTR_BLOCK, oldRef);
            blockedSnippets = new ArrayList<>(elem.select(blockedSnippetSelector));

            // find blocked embed
            blockedSnippetSelector = SelectorUtil.attr(ExtNodeConstants.EMBED_NODE_TAG_SELECTOR, ExtNodeConstants.SNIPPET_NODE_ATTR_BLOCK,
                    oldRef);
            blockedSnippets.addAll(elem.select(blockedSnippetSelector));

            for (Element be : blockedSnippets) {
                be.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_BLOCK, newRef);
            }
            element.attr(ExtNodeConstants.ATTR_SNIPPET_REF, newRef);
        }
    }

    public final static Element getEmbedNodeContent(Element elem) throws TemplateException, TemplateNotFoundException {
        String target;
        Configuration conf = Configuration.getConfiguration();
        TemplateResolver templateResolver = conf.getTemplateResolver();
        target = elem.attr(ExtNodeConstants.EMBED_NODE_ATTR_TARGET);
        if (target == null || target.isEmpty()) {
            String message = "Target not defined[" + elem.toString() + "]";
            throw new TemplateException(message);
        }
        Template embedTarget = templateResolver.findTemplate(target);

        // TODO all of the following process should be merged into template
        // analyze process and be cached.
        Document embedDoc = embedTarget.getDocumentClone();
        /*
                Elements children = embedDoc.body().children();
                Element wrappingNode = ElementUtil.wrapElementsToSingleNode(children);
        */
        Element wrappingNode = new GroupNode(ExtNodeConstants.GROUP_NODE_ATTR_TYPE_EMBED_WRAPPER);
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

        // a embed template file may by included many times in same parent
        // template, so we have to avoid duplicated snippet refs
        resetSnippetRefs(wrappingNode);

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
            } else if (!block.hasAttr("id")) {
                // TODO I want a approach to logging out template file path here
                logger.warn("The block does not declare its action or id correctlly.[{}]", block.toString());
                continue;
            } else {
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
