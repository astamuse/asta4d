package com.astamuse.asta4d.snippet.resolve;

import com.astamuse.asta4d.snippet.SnippetNotResovlableException;

public interface SnippetResolver {
    public Object findSnippet(String snippetName) throws SnippetNotResovlableException;
}
