package org.jsoupit.template;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoupit.Configuration;
import org.jsoupit.Context;
import org.jsoupit.template.extnode.ExtNodeConstants;
import org.jsoupit.template.extnode.GroupNode;
import org.jsoupit.template.extnode.SnippetNode;

public class Template {

    private String path;

    private Document doc;

    /**
     * 
     * @param path
     *            not being used, just for debug purpose
     * @param input
     * @throws IOException
     */
    public Template(String path, InputStream input) throws IOException {
        this.path = path;
        doc = Jsoup.parse(input, "UTF-8", "");
        initDocument();
    }

    private void initDocument() throws IOException {
        // find inject
        processInjection();
        // find embed
        processEmbed();
        // snippet
        processSnippet();
    }

    private void processInjection() throws IOException {
        Element injection = doc.select(ExtNodeConstants.INJECT_NODE_TAG_SELECTOR).first();
        if (injection != null) {
            String target = injection.attr(ExtNodeConstants.INJECT_NODE_ATTR_TARGET);
            if (target == null || target.isEmpty()) {
                throw new RuntimeException("You must specify target of a injection");
            }
            Configuration conf = Context.getCurrentThreadContext().getConfiguration();
            Template parent = conf.getTemplateResolver().findTemplate(target);
            Document parentDoc = parent.getDocumentClone();
            Iterator<Element> blockIterator = injection.select(ExtNodeConstants.BLOCK_NODE_TAG_SELECTOR).iterator();
            Element block, targetDock;
            String blockTarget;
            String targetDockSelector = ExtNodeConstants.DOCK_NODE_TAG_SELECTOR + "[" + ExtNodeConstants.DOCK_NODE_ATTR_NAME + "=%s]";
            while (blockIterator.hasNext()) {
                block = blockIterator.next();
                blockTarget = block.attr(ExtNodeConstants.BLOCK_NODE_ATTR_TARGET);
                if (blockTarget == null || blockTarget.isEmpty()) {
                    continue;
                }
                targetDock = parentDoc.select(String.format(targetDockSelector, blockTarget)).first();
                if (targetDock == null) {
                    continue;
                }

                targetDock.replaceWith(block);
            }
            RenderUtil.removeJsoupitNodes(parentDoc, ExtNodeConstants.DOCK_NODE_TAG_SELECTOR, false);
            RenderUtil.removeJsoupitNodes(parentDoc, ExtNodeConstants.BLOCK_NODE_TAG_SELECTOR, true);
            doc = parentDoc;
        }
    }

    private void processEmbed() throws IOException {
        Iterator<Element> embedIterator = doc.select(ExtNodeConstants.EMBED_NODE_TAG_SELECTOR).iterator();
        Element embed;
        String target;
        Configuration conf = Context.getCurrentThreadContext().getConfiguration();
        TemplateResolver templateResolver = conf.getTemplateResolver();
        while (embedIterator.hasNext()) {
            embed = embedIterator.next();
            target = embed.attr(ExtNodeConstants.EMBED_NODE_ATTR_TARGET);
            if (target == null || target.isEmpty()) {
                continue;
            }
            Template embedTarget = templateResolver.findTemplate(target);
            if (embedTarget == null) {
                embed.remove();
                continue;
            }

            Element groupNode = new GroupNode();
            embed.before(groupNode);
            embed.remove();
            Iterator<Attribute> attrs = embed.attributes().iterator();
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
        }
    }

    private void processSnippet() {
        // find nodes emebed with snippet attribute
        List<Element> embedSnippets = new ArrayList<>(doc.select("[" + ExtNodeConstants.SNIPPET_NODE_ATTR_NAME + "]"));
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

        // set all direct snippet node to not finished
        List<Element> snippetNodes = doc.select(ExtNodeConstants.SNIPPET_NODE_TAG_SELECTOR);
        for (Element sn : snippetNodes) {
            sn.attr(ExtNodeConstants.SNIPPET_NODE_ATTR_FINISHED, "false");
        }
    }

    public String getPath() {
        return path;
    }

    public Document getDocumentClone() {
        return doc.clone();
    }
}
