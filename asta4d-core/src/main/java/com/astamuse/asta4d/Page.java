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

    private final static List<PageInterceptorWrapper> WrapperPageInterceptorList = PageInterceptorWrapper.buildList(Configuration
            .getConfiguration().getPageInterceptorList());

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
