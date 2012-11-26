package com.astamuse.asta4d.snippet.extract;

import com.astamuse.asta4d.snippet.SnippetDeclarationInfo;

public interface SnippetExtractor {
    public SnippetDeclarationInfo extract(String renderDeclaration);
}
