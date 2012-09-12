package org.jsoupit.template.snippet.extract;

import org.jsoupit.template.snippet.SnippetDeclarationInfo;

public interface SnippetExtractor {
    public SnippetDeclarationInfo extract(String renderDeclaration);
}
