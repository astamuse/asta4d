package com.astamuse.asta4d.template;


/**
 * This template resolver is mostly used by test. However if you'd like to put
 * your template files in your source folder, you can use this resolver as well.
 * 
 * @author e-ryu
 * 
 */
public class ClasspathTemplateResolver extends TemplateResolver {

    @Override
    public TemplateInfo loadResource(String path) {
        return createTemplateInfo(path, this.getClass().getResourceAsStream(path));
    }

}
