package org.jsoupit.template;

import org.jsoupit.template.data.ContextDataFinder;
import org.jsoupit.template.data.DefaultContextDataFinder;
import org.jsoupit.template.snippet.DefaultSnippetInvoker;
import org.jsoupit.template.snippet.SnippetInvoker;
import org.jsoupit.template.snippet.extract.DefaultSnippetExtractor;
import org.jsoupit.template.snippet.extract.SnippetExtractor;
import org.jsoupit.template.snippet.resolve.DefaultSnippetResolver;
import org.jsoupit.template.snippet.resolve.SnippetResolver;

public class Configuration {

    private TemplateResolver templateResolver = new FileTemplateResolver();

    private SnippetInvoker snippetInvoker = new DefaultSnippetInvoker();

    private SnippetResolver snippetResolver = new DefaultSnippetResolver();

    private SnippetExtractor snippetExtractor = new DefaultSnippetExtractor();

    private ContextDataFinder contextDataFinder = new DefaultContextDataFinder();

    private boolean cacheEnable = true;

    public TemplateResolver getTemplateResolver() {
        return templateResolver;
    }

    public void setTemplateResolver(TemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }

    public SnippetInvoker getSnippetInvoker() {
        return snippetInvoker;
    }

    public void setSnippetInvoker(SnippetInvoker snippetInvoker) {
        this.snippetInvoker = snippetInvoker;
    }

    public SnippetResolver getSnippetResolver() {
        return snippetResolver;
    }

    public void setSnippetResolver(SnippetResolver snippetResolver) {
        this.snippetResolver = snippetResolver;
    }

    public SnippetExtractor getSnippetExtractor() {
        return snippetExtractor;
    }

    public void setSnippetExtractor(SnippetExtractor snippetExtractor) {
        this.snippetExtractor = snippetExtractor;
    }

    public ContextDataFinder getContextDataFinder() {
        return contextDataFinder;
    }

    public void setContextDataFinder(ContextDataFinder contextDataFinder) {
        this.contextDataFinder = contextDataFinder;
    }

    public boolean isCacheEnable() {
        return cacheEnable;
    }

    public void setCacheEnable(boolean cacheEnable) {
        this.cacheEnable = cacheEnable;
    }

}
