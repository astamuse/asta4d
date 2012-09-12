package org.jsoupit.template.snippet;

import org.jsoupit.template.render.Renderer;

public interface SnippetInvoker {
    public Renderer invoke(String renderDeclaration) throws SnippetNotResovlableException, SnippetInvokeException;
}
