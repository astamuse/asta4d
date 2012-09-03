package org.jsoupit.test.infra;

import org.jsoupit.template.ClasspathTemplateResolver;
import org.jsoupit.template.Configuration;
import org.jsoupit.template.Context;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class BaseTest {
    private final static Configuration configuration = new Configuration() {
        {
            this.setTemplateResolver(new ClasspathTemplateResolver());
        }
    };

    @BeforeMethod
    public void initContext() {
        System.out.println("initContext");
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
        System.out.println("clearContext");
        Context.getCurrentThreadContext()
                .clearSavedData();
    }

}
