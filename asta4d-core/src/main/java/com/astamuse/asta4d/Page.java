package com.astamuse.asta4d;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;

import com.astamuse.asta4d.interceptor.PageInterceptor;
import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.interceptor.base.Executor;
import com.astamuse.asta4d.interceptor.base.GenericInterceptor;
import com.astamuse.asta4d.interceptor.base.InterceptorUtil;
import com.astamuse.asta4d.render.GoThroughRenderer;
import com.astamuse.asta4d.render.RenderUtil;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.template.Template;
import com.astamuse.asta4d.template.TemplateResolver;

public class Page {

    private static class PageInterceptorWrapper implements GenericInterceptor<Document> {

        private PageInterceptor interceptor;

        public PageInterceptorWrapper(PageInterceptor interceptor) {
            this.interceptor = interceptor;
        }

        @Override
        public boolean beforeProcess(Document doc) throws Exception {
            Renderer renderer = new GoThroughRenderer();
            interceptor.prePageRendering(renderer);
            RenderUtil.apply(doc, renderer);
            return true;
        }

        @Override
        public void afterProcess(Document doc, ExceptionHandler exceptionHandler) {
            if (exceptionHandler.getException() != null) {
                return;
            }
            Renderer renderer = new GoThroughRenderer();
            interceptor.postPageRendering(renderer);
            RenderUtil.apply(doc, renderer);
        }

        public final static List<PageInterceptorWrapper> buildList(List<PageInterceptor> interceptorList) {
            List<PageInterceptorWrapper> list = new ArrayList<>();
            if (interceptorList != null) {
                for (PageInterceptor interceptor : interceptorList) {
                    list.add(new PageInterceptorWrapper(interceptor));
                }
            }
            return list;
        }

    }

    private final static List<PageInterceptorWrapper> WrapperPageInterceptorList = PageInterceptorWrapper.buildList(Context
            .getCurrentThreadContext().getConfiguration().getPageInterceptorList());

    private Template template;

    private Document renderedDocument;

    public Page(String path) throws Exception {
        Configuration conf = Context.getCurrentThreadContext().getConfiguration();
        TemplateResolver templateResolver = conf.getTemplateResolver();
        template = templateResolver.findTemplate(path);
        renderedDocument = renderTemplate(template);
    }

    protected Document renderTemplate(Template template) throws Exception {
        Document doc = template.getDocumentClone();

        InterceptorUtil.executeWithInterceptors(doc, WrapperPageInterceptorList, new Executor<Document>() {
            @Override
            public void execute(Document doc) throws Exception {
                RenderUtil.applySnippets(doc);
                RenderUtil.applyClearAction(doc, true);
            }
        });

        RenderUtil.applyMessages(doc);
        RenderUtil.applyClearAction(doc, true);
        return doc;
    }

    public Document getRenderedDocument() {
        return renderedDocument;
    }

    public void output(OutputStream out) throws Exception {
        out.write(renderedDocument.outerHtml().getBytes("utf-8"));
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
