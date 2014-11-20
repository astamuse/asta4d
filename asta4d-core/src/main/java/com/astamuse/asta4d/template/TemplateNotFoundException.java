package com.astamuse.asta4d.template;

public class TemplateNotFoundException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public TemplateNotFoundException(String path) {
        super("Template [" + path + "] does not exist.");
    }

    public TemplateNotFoundException(String path, String extraMsg) {
        super("Template [" + path + "] does not exist." + extraMsg);
    }

}
