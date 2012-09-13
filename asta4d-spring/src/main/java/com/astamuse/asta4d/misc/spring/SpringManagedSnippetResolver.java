package com.astamuse.asta4d.misc.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.astamuse.asta4d.snippet.resolve.DefaultSnippetResolver;

public class SpringManagedSnippetResolver extends DefaultSnippetResolver implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

    @Override
    protected Object loadResource(String name) {
        try {
            return applicationContext.getBean(name);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
