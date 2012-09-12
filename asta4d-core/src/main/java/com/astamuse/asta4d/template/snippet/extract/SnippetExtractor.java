package com.astamuse.asta4d.template.snippet.extract;

import com.astamuse.asta4d.template.snippet.SnippetDeclarationInfo;

public interface SnippetExtractor {
    public SnippetDeclarationInfo extract(String renderDeclaration);
}
