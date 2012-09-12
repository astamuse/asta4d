package com.astamuse.asta4d.template.snippet;

import com.astamuse.asta4d.template.render.Renderer;

public interface SnippetInvoker {
    public Renderer invoke(String renderDeclaration) throws SnippetNotResovlableException, SnippetInvokeException;
}
