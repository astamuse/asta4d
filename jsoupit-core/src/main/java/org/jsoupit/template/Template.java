package org.jsoupit.template;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.BlockTagSupportHtmlTreeBuilder;
import org.jsoup.parser.Parser;
import org.jsoupit.Configuration;
import org.jsoupit.Context;
import org.jsoupit.template.extnode.ExtNodeConstants;
import org.jsoupit.template.util.ElementUtil;
import org.jsoupit.template.util.SelectorUtil;
import org.jsoupit.template.util.TemplateUtil;

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
    public Template(String path, InputStream input) throws TemplateException {
        try {
            this.path = path;
            this.doc = Jsoup.parse(input, "UTF-8", "", new Parser(new BlockTagSupportHtmlTreeBuilder()));
            // this.doc = Jsoup.parse(input, "UTF-8", "");
            initDocument();
        } catch (IOException e) {
            throw new TemplateException(e);
        }
    }

    private void initDocument() throws TemplateException {
        // find inject
        processExtension();
        TemplateUtil.regulateElement(doc);
    }

    private void processExtension() throws TemplateException {
        Element extension = doc.select(ExtNodeConstants.EXTENSION_NODE_TAG_SELECTOR).first();
        if (extension != null) {
            String parentPath = extension.attr(ExtNodeConstants.EXTENSION_NODE_ATTR_PARENT);
            if (parentPath == null || parentPath.isEmpty()) {
                throw new RuntimeException("You must specify the parent of an extension");
            }
            Configuration conf = Context.getCurrentThreadContext().getConfiguration();
            Template parent = conf.getTemplateResolver().findTemplate(parentPath);
            Document parentDoc = parent.getDocumentClone();
            Iterator<Element> blockIterator = extension.select(ExtNodeConstants.BLOCK_NODE_TAG_SELECTOR).iterator();
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
                    // TODO log out warning
                    continue;
                }

                blockTarget = block.attr(blockType);
                if (blockTarget == null || blockTarget.isEmpty()) {
                    // TODO log out warning
                    continue;
                }
                targetBlock = parentDoc.select(SelectorUtil.id(ExtNodeConstants.BLOCK_NODE_TAG_SELECTOR, blockTarget)).first();
                if (targetBlock == null) {
                    // TODO log out warning
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

                // targetDock.replaceWith(block);
            }
            doc = parentDoc;
        }
    }

    public String getPath() {
        return path;
    }

    public Document getDocumentClone() {
        return doc.clone();
    }
}
