package com.astamuse.asta4d.template;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileTemplateResolver extends TemplateResolver {

    @Override
    protected TemplateInfo loadResource(String path) {
        try {
            return createTemplateInfo(path, new FileInputStream(path));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

}
