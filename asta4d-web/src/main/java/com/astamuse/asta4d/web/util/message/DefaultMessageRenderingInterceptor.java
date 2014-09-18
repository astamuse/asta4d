package com.astamuse.asta4d.web.util.message;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.astamuse.asta4d.interceptor.PageInterceptor;
import com.astamuse.asta4d.render.ElementNotFoundHandler;
import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.template.Template;
import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.util.ElementUtil;
import com.astamuse.asta4d.web.WebApplicationConfiguration;

public class DefaultMessageRenderingInterceptor implements PageInterceptor {

    private Elements cachedSnippet = null;

    public DefaultMessageRenderingInterceptor() {
        super();
    }

    @Override
    public void prePageRendering(Renderer renderer) {

    }

    @Override
    public void postPageRendering(Renderer renderer) {
        final WebApplicationConfiguration configuration = WebApplicationConfiguration.getWebApplicationConfiguration();
        Renderer msgRenderer = retrieveMessageRenderingHelper().createMessageRenderer();

        if (msgRenderer != null) {
            renderer.add(new ElementNotFoundHandler(configuration.getMessageGlobalContainerSelector()) {
                @Override
                public Renderer alternativeRenderer() {
                    // add global message container if not exists
                    return Renderer.create(configuration.getMessageGlobalContainerParentSelector(), new ElementSetter() {
                        @Override
                        public void set(Element elem) {
                            List<Element> elems = new ArrayList<>(retrieveCachedContainerSnippet());
                            Collections.reverse(elems);
                            for (Element child : elems) {
                                elem.prependChild(ElementUtil.safeClone(child));
                            }
                        }
                    });
                }// alternativeRenderer
            });// ElementNotFoundHandler

            renderer.add(msgRenderer);
        }
    }

    protected Elements retrieveCachedContainerSnippet() {
        if (WebApplicationConfiguration.getWebApplicationConfiguration().isCacheEnable()) {
            if (cachedSnippet == null) {
                cachedSnippet = retrieveContainerSnippet();
            }
            return cachedSnippet;
        } else {
            return retrieveContainerSnippet();
        }

    }

    protected Elements retrieveContainerSnippet() {
        WebApplicationConfiguration conf = WebApplicationConfiguration.getWebApplicationConfiguration();
        String path = conf.getMessageGlobalContainerSnippetFilePath();
        Template template;
        try {
            // at first, we treat the configured snippet file as a template file
            template = conf.getTemplateResolver().findTemplate(path);
            if (template == null) {
                // then treat it as classpath resource
                InputStream input = this.getClass().getClassLoader().getResourceAsStream(path);
                if (input == null) {
                    throw new NullPointerException("Configured message container snippet file[" + path + "] was not found");
                }
                template = new Template(path, input);
            }
            return template.getDocumentClone().body().children();
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    protected MessageRenderingHelper retrieveMessageRenderingHelper() {
        return DefaultMessageRenderingHelper.instance();
    }

}
