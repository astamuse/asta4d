package com.astamuse.asta4d.misc.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

public class Asta4dBeanNameGenerator extends AnnotationBeanNameGenerator {

    private String[] snippetSearchPathList;

    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        if (snippetSearchPathList == null) {
            return super.buildDefaultBeanName(definition);
        }
        String clsName = definition.getBeanClassName();
        for (String s : snippetSearchPathList) {
            if (clsName.startsWith(s)) {
                return clsName.substring(s.length());
            }
        }
        return super.buildDefaultBeanName(definition);
    }

    public void setSnippetSearchPathList(String... snippetSearchPathList) {
        this.snippetSearchPathList = snippetSearchPathList.clone();
    }

}
