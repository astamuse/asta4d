package org.jsoupit.template.snippet.extract;

import org.jsoupit.template.snippet.SnippetInfo;

public interface SnippetExtractor {
    public SnippetInfo extract(String renderDeclaration);
}
