package com.astamuse.asta4d.test.render.infra;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.snippet.resolve.DefaultSnippetResolver;
import com.astamuse.asta4d.template.ClasspathTemplateResolver;

@Test
public class BaseTest {

    public final static String ReverseInjectableScope = "ReverseInjectableScope";

    private final static Configuration configuration = new Configuration() {
        {
            ClasspathTemplateResolver templateResolver = new ClasspathTemplateResolver();
            List<String> templateBaseFolders = Arrays.asList("/com/astamuse/asta4d/test/render/templates");
            templateResolver.setSearchPathList(templateBaseFolders);
            this.setTemplateResolver(templateResolver);

            DefaultSnippetResolver snippetResolver = new DefaultSnippetResolver();
            List<String> snippetBasePackages = Arrays.asList("com.astamuse.asta4d.test.render");
            snippetResolver.setSearchPathList(snippetBasePackages);
            this.setSnippetResolver(snippetResolver);

            this.setReverseInjectableScopes(Arrays.asList(ReverseInjectableScope));
        }
    };

    @BeforeMethod
    public void initContext() {
        Context context = Context.getCurrentThreadContext();
        if (context == null) {
            context = new Context();
            context.setConfiguration(configuration);
            Context.setCurrentThreadContext(context);

        }
        context.clearSavedData();
    }

    @AfterMethod
    public void clearContext() {
        Context.getCurrentThreadContext().clearSavedData();
    }

}
