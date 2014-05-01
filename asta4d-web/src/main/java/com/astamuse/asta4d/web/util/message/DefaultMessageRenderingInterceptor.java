package com.astamuse.asta4d.web.util.message;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Element;

import com.astamuse.asta4d.interceptor.PageInterceptor;
import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.web.WebApplicationConfiguration;

public class DefaultMessageRenderingInterceptor implements PageInterceptor {

    private String messageContainerParentSelector = "body";

    private String messageContainerSnippetFile = "/com/astamuse/asta4d/web/util/message/DefaultMessageContainerSnippet.html";

    private String cachedSnippet = null;

    public DefaultMessageRenderingInterceptor() {
        super();
    }

    public DefaultMessageRenderingInterceptor(String messageContainerParentSelector, String messageContainerSnippetFile) {
        super();
        this.messageContainerParentSelector = messageContainerParentSelector;
        this.messageContainerSnippetFile = messageContainerSnippetFile;
    }

    @Override
    public void prePageRendering(Renderer renderer) {
        renderer.add(messageContainerParentSelector, new ElementSetter() {
            @Override
            public void set(Element elem) {
                elem.prepend(retrieveContainerSnippet());
            }
        });
    }

    @Override
    public void postPageRendering(Renderer renderer) {
        Renderer msgRenderer = retrieveMessageRenderingHelper().createMessageRenderer();
        if (msgRenderer != null) {
            renderer.add(msgRenderer);
        }
    }

    protected String retrieveContainerSnippet() {
        if (cachedSnippet == null) {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream(messageContainerSnippetFile);
            try {
                String snippet = IOUtils.toString(stream, StandardCharsets.UTF_8);
                if (WebApplicationConfiguration.getWebApplicationConfiguration().isCacheEnable()) {
                    cachedSnippet = snippet;
                }
                return snippet;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            return cachedSnippet;
        }

    }

    protected MessageRenderingHelper retrieveMessageRenderingHelper() {
        return DefaultMessageRenderingHelper.instance();
    }

    public void setMessageContainerParentSelector(String messageContainerParentSelector) {
        this.messageContainerParentSelector = messageContainerParentSelector;
    }

    public void setMessageContainerSnippetFile(String messageContainerSnippetFile) {
        this.messageContainerSnippetFile = messageContainerSnippetFile;
    }

}
