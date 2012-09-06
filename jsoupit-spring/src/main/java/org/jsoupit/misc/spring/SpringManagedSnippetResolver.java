package org.jsoupit.misc.spring;

import org.jsoupit.template.snippet.SnippetNotResovlableException;
import org.jsoupit.template.snippet.resolve.SnippetResolver;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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
