package com.astamuse.asta4d.web.test;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.snippet.resolve.DefaultSnippetResolver;
import com.astamuse.asta4d.template.ClasspathTemplateResolver;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;

public class WebTestBase {
    private final static WebApplicationConfiguration configuration = new WebApplicationConfiguration() {
        {
            ClasspathTemplateResolver templateResolver = new ClasspathTemplateResolver();
            List<String> templateBaseFolders = Arrays.asList("/com/astamuse/asta4d/web/test/render/templates",
                    "/com/astamuse/asta4d/web/test/form/templates");
            templateResolver.setSearchPathList(templateBaseFolders);
            this.setTemplateResolver(templateResolver);

            DefaultSnippetResolver snippetResolver = new DefaultSnippetResolver();
            List<String> snippetBasePackages = Arrays.asList("com.astamuse.asta4d.web.test.render", "com.astamuse.asta4d.web.test.form");
            snippetResolver.setSearchPathList(snippetBasePackages);
            this.setSnippetResolver(snippetResolver);

            this.setOutputAsPrettyPrint(true);

            this.setSaveCallstackInfoOnRendererCreation(true);
        }
    };
    static {
        Configuration.setConfiguration(configuration);
    }

    @BeforeMethod
    public void initContext() {
        Configuration.setConfiguration(configuration);
        Context context = Context.getCurrentThreadContext();
        if (context == null) {
            context = new WebApplicationContext();
            Context.setCurrentThreadContext(context);

        }
        context.init();
    }

    @AfterMethod
    public void clearContext() {
        Context.getCurrentThreadContext().clear();
    }
}
