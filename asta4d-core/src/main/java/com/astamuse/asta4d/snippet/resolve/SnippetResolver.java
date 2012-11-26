package com.astamuse.asta4d.snippet.resolve;

import com.astamuse.asta4d.snippet.SnippetDeclarationInfo;
import com.astamuse.asta4d.snippet.SnippetExcecutionInfo;
import com.astamuse.asta4d.snippet.SnippetNotResovlableException;

public interface SnippetResolver {
    public SnippetExcecutionInfo resloveSnippet(SnippetDeclarationInfo declaration) throws SnippetNotResovlableException;
}
