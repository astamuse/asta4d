package com.astamuse.asta4d.template.snippet.resolve;

import com.astamuse.asta4d.template.snippet.SnippetNotResovlableException;

public interface SnippetResolver {
    public Object findSnippet(String snippetName) throws SnippetNotResovlableException;
}
