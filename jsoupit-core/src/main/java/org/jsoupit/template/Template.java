package org.jsoupit.template;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoupit.Configuration;
import org.jsoupit.Context;
import org.jsoupit.template.extnode.ExtNodeConstants;
import org.jsoupit.template.util.RenderUtil;
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
            this.doc = Jsoup.parse(input, "UTF-8", "");
            initDocument();
        } catch (IOException e) {
            throw new TemplateException(e);
        }
    }

    private void initDocument() throws TemplateException {
        // find inject
        processInjection();
        TemplateUtil.regulateElement(doc);
    }

    private void processInjection() throws TemplateException {
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

    public String getPath() {
        return path;
    }

    public Document getDocumentClone() {
        return doc.clone();
    }
}
