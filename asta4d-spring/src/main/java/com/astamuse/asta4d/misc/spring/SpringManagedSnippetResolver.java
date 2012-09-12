package com.astamuse.asta4d.misc.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.astamuse.asta4d.template.snippet.SnippetNotResovlableException;
import com.astamuse.asta4d.template.snippet.resolve.SnippetResolver;

public class SpringManagedSnippetResolver implements SnippetResolver, ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public Object findSnippet(String snippetName) throws SnippetNotResovlableException {
        try {
            return applicationContext.getBean(snippetName);
        } catch (Exception ex) {
            throw new SnippetNotResovlableException(String.format("snippet [%s] not found", snippetName), ex);
        }
    }

    @SuppressWarnings("static-access")
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

}
