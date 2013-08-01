package com.astamuse.asta4d.web.dispatch.request.transformer;

public class TemplateNotFoundException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public TemplateNotFoundException(String path) {
        super("Template [" + path + "] does not exist.");
    }

}
