package com.astamuse.asta4d.snippet;

public class SnippetInvokeException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 115458825987835218L;

    public SnippetInvokeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SnippetInvokeException(SnippetDeclarationInfo declaration, String msg) {
        super(createMsg(declaration, msg));
    }

    public SnippetInvokeException(SnippetDeclarationInfo declaration) {
        super(createMsg(declaration, null));
    }

    public SnippetInvokeException(SnippetDeclarationInfo declaration, Throwable cause) {
        super(createMsg(declaration, null), cause);
    }

    public SnippetInvokeException(SnippetDeclarationInfo declaration, String msg, Throwable cause) {
        super(createMsg(declaration, msg), cause);
    }

    private static String createMsg(SnippetDeclarationInfo declaration, String msg) {
        return "error occured when execute snippet " + declaration.toString() + (msg == null ? "" : " detail:" + msg);
    }

}
