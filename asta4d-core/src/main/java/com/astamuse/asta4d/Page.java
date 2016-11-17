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

package com.astamuse.asta4d;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.astamuse.asta4d.extnode.ExtNodeConstants;
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
import com.astamuse.asta4d.util.SelectorUtil;

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

    private final static List<PageInterceptorWrapper> WrapperPageInterceptorList = PageInterceptorWrapper
            .buildList(Configuration.getConfiguration().getPageInterceptorList());

    protected final static String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

    private Document renderedDocument;

    public Page(Template template) throws Exception {
        renderedDocument = renderTemplate(template);
    }

    public final static Page buildFromPath(String path) throws Exception {
        Configuration conf = Configuration.getConfiguration();
        TemplateResolver templateResolver = conf.getTemplateResolver();
        Template template = templateResolver.findTemplate(path);
        return new Page(template);
    }

    protected Document renderTemplate(Template template) throws Exception {
        Configuration conf = Configuration.getConfiguration();
        Document doc = template.getDocumentClone();
        doc.outputSettings().prettyPrint(conf.isOutputAsPrettyPrint());
        InterceptorUtil.executeWithInterceptors(doc, WrapperPageInterceptorList, new Executor<Document>() {
            @Override
            public void execute(Document doc) throws Exception {
                RenderUtil.applySnippets(doc);
                RenderUtil.applyMessages(doc);
                RenderUtil.applyClearAction(doc, true);
            }
        });
        // clear after page interceptors was executed
        RenderUtil.applyClearAction(doc, true);
        return doc;
    }

    protected Document getRenderedDocument() {
        return renderedDocument;
    }

    public String getContentType() {
        Elements elems = renderedDocument.select("meta[http-equiv=Content-Type]");
        if (elems.size() == 0) {
            return DEFAULT_CONTENT_TYPE;
        } else {
            return elems.get(0).attr("content");
        }
    }

    public void output(OutputStream out) throws Exception {
        out.write(output().getBytes(StandardCharsets.UTF_8));
    }

    public String output() {

        // body only attr on body
        if (renderedDocument.body().hasAttr(ExtNodeConstants.ATTR_BODY_ONLY_WITH_NS)) {
            return renderedDocument.body().html();
        }

        // body only meta
        Elements bodyonlyMeta = renderedDocument.head().select(SelectorUtil.attr("meta", ExtNodeConstants.ATTR_BODY_ONLY_WITH_NS, null));
        if (bodyonlyMeta.size() > 0) {
            return renderedDocument.body().html();
        }

        // full page
        return renderedDocument.outerHtml();
    }

    /**
     * This method is for back forward compatibility in framework internal implementation, client developers should never use it.
     * 
     * @param out
     * @throws Exception
     */
    @Deprecated
    public void outputBodyOnly(OutputStream out) throws Exception {
        out.write(renderedDocument.body().html().getBytes(StandardCharsets.UTF_8));

    }
}
