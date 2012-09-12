package com.astamuse.asta4d.template;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileTemplateResolver extends TemplateResolver {

    @Override
    protected InputStream loadResource(String path) {
        try {
            return (new FileInputStream(path));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

}
