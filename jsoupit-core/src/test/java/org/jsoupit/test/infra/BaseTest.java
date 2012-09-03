package org.jsoupit.test.infra;

import org.jsoupit.template.ClasspathTemplateResolver;
import org.jsoupit.template.Configuration;
import org.jsoupit.template.Context;
import org.junit.After;
import org.junit.Before;

public class BaseTest {
    private final static Configuration configuration = new Configuration() {
        {
            this.setTemplateResolver(new ClasspathTemplateResolver());
        }
    };

    @Before
    public void initContext() {
        Context context = Context.getCurrentThreadContext();
        if (context == null) {
            context = new Context();
            context.setConfiguration(configuration);
            Context.setCurrentThreadContext(context);
        }
        context.clearSavedData();
    }

    @After
    public void clearContext() {
        Context.getCurrentThreadContext().clearSavedData();
    }

}
