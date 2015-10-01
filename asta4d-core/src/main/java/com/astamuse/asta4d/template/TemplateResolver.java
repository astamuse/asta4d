package com.astamuse.asta4d.template;

public interface TemplateResolver {
    /**
     * 
     * @param path
     * @return
     * @throws TemplateException
     *             error occurs when parsing template file
     * @throws TemplateNotFoundException
     *             template file does not exist
     */
    public Template findTemplate(String path) throws TemplateException, TemplateNotFoundException;
}
