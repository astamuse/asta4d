package org.jsoupit.template;

import java.io.InputStream;

/**
 * This template resolver is mostly used by test. However if you'd like to put
 * your template files in your source folder, you can use this resolver as well.
 * 
 * @author e-ryu
 * 
 */
public class ClasspathTemplateResolver extends TemplateResolver {

    @Override
    public InputStream loadTemplate(String path) {
        return this.getClass().getResourceAsStream(path);
    }

    public void setBasePackage() {
        // TODO implement it
    }

}
