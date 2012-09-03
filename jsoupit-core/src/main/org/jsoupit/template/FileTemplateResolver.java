package org.jsoupit.template;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileTemplateResolver extends TemplateResolver {

    private String baseFolder = "";

    @Override
    protected InputStream loadTemplate(String path) {
        String templatePath = baseFolder + "/" + path;
        try {
            return (new FileInputStream(templatePath));
        } catch (FileNotFoundException e) {
            // TODO debug exception
            return null;
        }
    }

    public void setBaseFolder(String baseFolder) {
        this.baseFolder = baseFolder;
    }
}
