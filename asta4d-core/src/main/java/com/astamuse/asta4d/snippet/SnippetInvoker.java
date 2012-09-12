package com.astamuse.asta4d.snippet;

import com.astamuse.asta4d.render.Renderer;

public interface SnippetInvoker {
    public Renderer invoke(String renderDeclaration) throws SnippetNotResovlableException, SnippetInvokeException;
}
