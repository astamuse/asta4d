package org.jsoupit.template.snippet.resolve;

import org.jsoupit.template.snippet.SnippetNotResovlableException;

public interface SnippetResolver {
    public Object findSnippet(String snippetName) throws SnippetNotResovlableException;
}
