package org.jsoupit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jsoup.nodes.Document;
import org.jsoupit.template.RenderUtil;
import org.jsoupit.template.Template;
import org.jsoupit.template.TemplateResolver;
import org.jsoupit.template.render.Renderer;

public class Page {

    protected Template template;

    public Page(String path) throws IOException {
        Configuration conf = Context.getCurrentThreadContext().getConfiguration();
        TemplateResolver templateResolver = conf.getTemplateResolver();
        template = templateResolver.findTemplate(path);
    }

    public void output(OutputStream out, Renderer renderer) throws Exception {
        Document doc = template.getDocumentClone();
        RenderUtil.applySnippets(doc);
        if (renderer != null) {
            RenderUtil.apply(doc, renderer);
        }
        RenderUtil.applyClearAction(doc);
        out.write(doc.outerHtml().getBytes("utf-8"));
    }

    public void output(OutputStream out) throws Exception {
        output(out, null);
    }

    public String output() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            output(bos);
            return bos.toString("utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();

        }

    }
}
