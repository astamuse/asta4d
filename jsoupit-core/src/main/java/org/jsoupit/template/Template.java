package org.jsoupit.template;

import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.BlockTagSupportHtmlTreeBuilder;
import org.jsoup.parser.Parser;
import org.jsoupit.Configuration;
import org.jsoupit.Context;
import org.jsoupit.template.extnode.ExtNodeConstants;
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
            TemplateUtil.mergeBlock(parentDoc, extension);
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
